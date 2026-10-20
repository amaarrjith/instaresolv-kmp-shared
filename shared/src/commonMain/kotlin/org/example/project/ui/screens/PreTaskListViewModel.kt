package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.model.PreTaskData
import org.example.project.data.model.PreTaskListRequest
import org.example.project.domain.repository.PreTaskRepository
import org.example.project.network.NetworkResult
import org.example.project.data.model.FilterContentData
import org.example.project.data.settings.AuthPreferences
import org.example.project.data.repository.PreTaskDraftRepository
import org.example.project.shared.db.PreTaskDraft

data class PreTaskListState(
    val isPullDown: Boolean = false,
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val preTasks: List<PreTaskData> = emptyList(),
    val searchKey: String = "",
    val error: String? = null,
    val endReached: Boolean = false,
    val appliedFilterState: org.example.project.data.model.AppFilterState = org.example.project.data.model.AppFilterState(),
    val errorExcel: String? = null,
    val drafts: List<PreTaskDraft> = emptyList()
)

class PreTaskListViewModel(
    private val repository: PreTaskRepository,
    private val authPreferences: AuthPreferences,
    private val preTaskDraftRepository: PreTaskDraftRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreTaskListState())
    val uiState: StateFlow<PreTaskListState> = _uiState.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _exportToastMessage = MutableStateFlow<String?>(null)
    val exportToastMessage: StateFlow<String?> = _exportToastMessage.asStateFlow()

    private val _exportUrl = MutableStateFlow<String?>(null)
    val exportUrl: StateFlow<String?> = _exportUrl.asStateFlow()

    fun clearExportToast() {
        _exportToastMessage.value = null
    }

    fun clearExportUrl() {
        _exportUrl.value = null
    }

    fun setExportToastMessage(message: String) {
        _exportToastMessage.value = message
    }

    fun clearErrorExcel() {
        _uiState.update { it.copy(errorExcel = null) }
    }

    fun setErrorExcel(message: String?) {
        _uiState.update { it.copy(errorExcel = message) }
    }

    val draftToastMessage = MutableStateFlow<String?>(null)

    private var currentPage = 1
    private var searchJob: Job? = null

    init {
        fetchPreTasks(isRefresh = true)
    }

    fun updateSearchKey(query: String) {
        _uiState.update { it.copy(searchKey = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // debounce
            fetchPreTasks(isRefresh = true)
        }
    }

    fun applyFilters(state: org.example.project.data.model.AppFilterState) {
        _uiState.update { it.copy(appliedFilterState = state) }
        fetchPreTasks(isRefresh = true)
    }

    fun fetchPreTasks(isRefresh: Boolean = false, isPullDown: Boolean = false) {
        if (isRefresh || isPullDown) {
            currentPage = 1
        }
        
        if (_uiState.value.isLoading || _uiState.value.isPaginating) return
        if (!isRefresh && _uiState.value.endReached) return

        if (isRefresh) {
            _uiState.update { it.copy(isLoading = true, error = null, endReached = false, isPullDown = false) }
        } else if (isPullDown) {
            _uiState.update { it.copy(isLoading = false, error = null, endReached = false, isPullDown = true) }
        } else {
            _uiState.update { it.copy(isPaginating = true, error = null) }
        }
        
        viewModelScope.launch {
            val filter = _uiState.value.appliedFilterState

            val request = PreTaskListRequest(
                pageNumber = currentPage,
                limit = 20,
                searchKey = _uiState.value.searchKey,
                reportedByPersons = filter.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() },
                projectIds = if (filter.noProjectSelected) {
                    listOf(-1)
                } else {
                    filter.selectedProjects.mapNotNull { it.groupId.toIntOrNull() }
                },
                openDate = formatMillis(filter.dateOpenMillis),
                endDate = formatMillis(filter.dateCloseMillis)
            )
            when (val result = repository.getPreTaskList(request)) {
                is NetworkResult.Success -> {
                    val items = result.data
                    _uiState.update { currentState ->
                        if (currentPage == 1) {
                            currentState.copy(
                                isPullDown = false,
                                isLoading = false,
                                isPaginating = false,
                                preTasks = items,
                                endReached = items.isEmpty()
                            )
                        } else {
                            val existingIds = currentState.preTasks.mapNotNull { it.id }.toSet()
                            val uniqueNewItems = items.filter { it.id !in existingIds }
                            
                            currentState.copy(
                                isPullDown = false,
                                isLoading = false,
                                isPaginating = false,
                                preTasks = currentState.preTasks + uniqueNewItems,
                                endReached = items.isEmpty() || uniqueNewItems.isEmpty()
                            )
                        }
                    }
                    if (items.isNotEmpty()) {
                        currentPage++
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isPullDown = false,
                            isLoading = false,
                            isPaginating = false,
                            error = result.message ?: "Failed to fetch pre tasks"
                        ) 
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun formatMillis(millis: Long?): String {
        if (millis == null) return ""
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
        val localDate = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        val day = localDate.dayOfMonth.toString().padStart(2, '0')
        val month = localDate.monthNumber.toString().padStart(2, '0')
        val year = localDate.year
        return "$day-$month-$year"
    }

    fun clearDraftToast() {
        draftToastMessage.value = null
    }

    fun loadDrafts() {
        viewModelScope.launch {
            val userId = authPreferences.getLoggedInUser()?.userId ?: return@launch
            val list = preTaskDraftRepository.getAllDrafts(userId)
            _uiState.update { it.copy(drafts = list) }
        }
    }

    fun deleteDrafts(ids: List<Long>, successMessage: String) {
        viewModelScope.launch {
            preTaskDraftRepository.deleteDrafts(ids)
            draftToastMessage.value = successMessage
            loadDrafts()
        }
    }

    fun exportToExcel() {
        viewModelScope.launch {
            _isExporting.value = true
            val currentState = _uiState.value
            val filter = currentState.appliedFilterState

            val request = PreTaskListRequest(
                pageNumber = 1,
                limit = 1000,
                sortType = 1,
                searchKey = currentState.searchKey,
                reportedByPersons = filter.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() },
                projectIds = if (filter.noProjectSelected) {
                    listOf(-1)
                } else {
                    filter.selectedProjects.mapNotNull { it.groupId.toIntOrNull() }
                },
                openDate = formatMillis(filter.dateOpenMillis),
                endDate = formatMillis(filter.dateCloseMillis)
            )

            when (val result = repository.generatePreTaskExcel(request)) {
                is NetworkResult.Success -> {
                    result.data.excelUrl?.takeIf { it.isNotBlank() }?.let {
                        _exportUrl.value = it
                    }
                }
                is NetworkResult.Error -> {
                    _exportToastMessage.value = result.message ?: "Export failed"
                }
            }
            _isExporting.value = false
        }
    }
}
