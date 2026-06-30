package org.example.project.ui.screens
import kotlinx.datetime.toLocalDateTime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.example.project.data.model.ObservationItem
import org.example.project.data.model.ObservationRequest
import org.example.project.domain.repository.ObservationRepository
import org.example.project.network.NetworkResult
import org.example.project.data.model.FilterContentData

data class ObservationListState(
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val observations: List<ObservationItem> = emptyList(),
    val searchKey: String = "",
    val error: String? = null,
    val endReached: Boolean = false,
    val filterData: FilterContentData? = null,
    val appliedFilterState: org.example.project.data.model.AppFilterState = org.example.project.data.model.AppFilterState(),
    var errorExcel: String? = null
)

class ObservationListViewModel(
    private val repository: ObservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ObservationListState())
    val uiState: StateFlow<ObservationListState> = _uiState.asStateFlow()
    
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
        fetchObservations(isRefresh = true)
        fetchFilterContent()
    }

    private fun fetchFilterContent() {
        viewModelScope.launch {
            when (val result = repository.getFilterContent()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(filterData = result.data) }
                }
                is NetworkResult.Error -> {
                    println("Filter API Error: ${result.message} - ${result.errorCode}")
                }
            }
        }
    }

    fun updateSearchKey(query: String) {
        _uiState.update { it.copy(searchKey = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // debounce
            fetchObservations(isRefresh = true)
        }
    }

    fun applyFilters(state: org.example.project.data.model.AppFilterState) {
        _uiState.update { it.copy(appliedFilterState = state) }
        fetchObservations(isRefresh = true)
    }

    fun fetchObservations(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
        }
        
        if (_uiState.value.isLoading || _uiState.value.isPaginating) return
        if (!isRefresh && _uiState.value.endReached) return

        if (currentPage == 1) {
            _uiState.update { it.copy(isLoading = true, error = null, endReached = false) }
        } else {
            _uiState.update { it.copy(isPaginating = true, error = null) }
        }
        
        viewModelScope.launch {
            val filter = _uiState.value.appliedFilterState
            val status = when {
                filter.selectedStatuses.contains("Open Observations") && filter.selectedStatuses.contains("Closed Observations") -> -1
                filter.selectedStatuses.contains("Open Observations") -> 1
                filter.selectedStatuses.contains("Closed Observations") -> 2
                else -> -1
            }
            val groupSpecified = when {
                filter.noProjectSelected -> 0
                filter.selectedProjects.isNotEmpty() -> 1
                else -> -1
            }

            val request = ObservationRequest(
                pageNumber = currentPage,
                searchKey = _uiState.value.searchKey,
                observers = filter.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() },
                responsiblePersons = filter.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() },
                groupIds = filter.selectedProjects.mapNotNull { it.groupId.toIntOrNull() },
                status = status,
                groupSpecified = groupSpecified,
                openDate = formatMillis(filter.dateOpenMillis),
                closeDate = formatMillis(filter.dateCloseMillis)
            )
            when (val result = repository.getObservationList(request)) {
                is NetworkResult.Success -> {
                    val items = result.data.observations
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isPaginating = false,
                            observations = if (currentPage == 1) items else it.observations + items,
                            endReached = items.isEmpty()
                        ) 
                    }
                    if (items.isNotEmpty()) {
                        currentPage++
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            isPaginating = false,
                            error = result.message ?: "Failed to fetch observations"
                        ) 
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun exportToExcel() {
        viewModelScope.launch {
            _isExporting.value = true
            val filter = _uiState.value.appliedFilterState
            val status = when {
                filter.selectedStatuses.contains("Open Observations") && filter.selectedStatuses.contains("Closed Observations") -> -1
                filter.selectedStatuses.contains("Open Observations") -> 1
                filter.selectedStatuses.contains("Closed Observations") -> 2
                else -> -1
            }
            val groupSpecified = when {
                filter.noProjectSelected -> 0
                filter.selectedProjects.isNotEmpty() -> 1
                else -> -1
            }

            val request = ObservationRequest(
                pageNumber = 1,
                searchKey = _uiState.value.searchKey,
                observers = filter.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() },
                responsiblePersons = filter.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() },
                groupIds = filter.selectedProjects.mapNotNull { it.groupId.toIntOrNull() },
                status = status,
                groupSpecified = groupSpecified,
                openDate = formatMillis(filter.dateOpenMillis),
                closeDate = formatMillis(filter.dateCloseMillis)
            )

            when (val result = repository.generateObservationExcel(request)) {
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

    private fun formatMillis(millis: Long?): String {
        if (millis == null) return ""
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
        val localDate = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date
        val day = localDate.dayOfMonth.toString().padStart(2, '0')
        val month = localDate.monthNumber.toString().padStart(2, '0')
        val year = localDate.year
        return "$day-$month-$year"
    }
}
