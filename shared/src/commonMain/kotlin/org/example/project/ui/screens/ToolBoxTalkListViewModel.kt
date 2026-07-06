package org.example.project.ui.screens

import kotlinx.datetime.toLocalDateTime
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.ToolBoxTalkListRequest
import org.example.project.data.model.ToolBoxTalkExcelRequest
import org.example.project.data.model.ToolBoxTalkItem
import org.example.project.domain.repository.ToolBoxTalkRepository
import org.example.project.network.NetworkResult
import org.example.project.data.model.AppFilterState

data class ToolBoxTalkListState(
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val items: List<ToolBoxTalkItem> = emptyList(),
    val searchKey: String = "",
    val error: String? = null,
    val endReached: Boolean = false,
    val appliedFilterState: AppFilterState = AppFilterState()
)

class ToolBoxTalkListViewModel(
    private val repository: ToolBoxTalkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ToolBoxTalkListState())
    val uiState: StateFlow<ToolBoxTalkListState> = _uiState.asStateFlow()

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

    private var currentPage = 1
    private var searchJob: Job? = null

    init {
        fetchToolBoxTalks(isRefresh = true)
    }

    fun updateSearchKey(query: String) {
        _uiState.update { it.copy(searchKey = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // debounce
            fetchToolBoxTalks(isRefresh = true)
        }
    }

    fun applyFilters(state: AppFilterState) {
        _uiState.update { it.copy(appliedFilterState = state) }
        fetchToolBoxTalks(isRefresh = true)
    }

    fun fetchToolBoxTalks(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
        }
        
        if (_uiState.value.isLoading || _uiState.value.isPaginating) return
        if (!isRefresh && _uiState.value.endReached) return

        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isLoading = true, error = null, endReached = false) }
            } else {
                _uiState.update { it.copy(isPaginating = true, error = null) }
            }

            val request = ToolBoxTalkListRequest(
                searchKey = _uiState.value.searchKey.takeIf { it.isNotBlank() },
                pageNumber = currentPage,
                limit = 20,
                sortType = 1,
                projectIds = _uiState.value.appliedFilterState.selectedProjects.mapNotNull { it.groupId.toIntOrNull() }.takeIf { it.isNotEmpty() },
                openDate = _uiState.value.appliedFilterState.dateOpenMillis?.let { formatDate(it) } ?: "",
                endDate = _uiState.value.appliedFilterState.dateCloseMillis?.let { formatDate(it) } ?: "",
                reportedByPersons = _uiState.value.appliedFilterState.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() }.takeIf { it.isNotEmpty() }
            )

            when (val result = repository.getToolBoxTalkList(request)) {
                is NetworkResult.Success -> {
                    val newItems = result.data
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isPaginating = false,
                            items = if (isRefresh) newItems else state.items + newItems,
                            endReached = newItems.isEmpty() || newItems.size < 20
                        )
                    }
                    if (newItems.isNotEmpty()) currentPage++
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPaginating = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun exportToExcel() {
        viewModelScope.launch {
            _isExporting.value = true

            val request = ToolBoxTalkExcelRequest(
                searchKey = _uiState.value.searchKey.takeIf { it.isNotBlank() },
                sortBy = 1,
                projectIds = _uiState.value.appliedFilterState.selectedProjects.mapNotNull { it.groupId.toIntOrNull() }.takeIf { it.isNotEmpty() },
                openDate = _uiState.value.appliedFilterState.dateOpenMillis?.let { formatDate(it) } ?: "",
                endDate = _uiState.value.appliedFilterState.dateCloseMillis?.let { formatDate(it) } ?: "",
                reportedByPersons = _uiState.value.appliedFilterState.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() }.takeIf { it.isNotEmpty() }
            )

            when (val result = repository.generateToolBoxTalkExcel(request)) {
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
    
    private fun formatDate(millis: Long): String {
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
        val tz = kotlinx.datetime.TimeZone.currentSystemDefault()
        val dt = instant.toLocalDateTime(tz)
        return "${dt.dayOfMonth.toString().padStart(2, '0')}-${dt.monthNumber.toString().padStart(2, '0')}-${dt.year}"
    }
}
