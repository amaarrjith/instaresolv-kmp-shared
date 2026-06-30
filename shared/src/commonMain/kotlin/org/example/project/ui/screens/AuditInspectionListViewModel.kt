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
import org.example.project.data.model.InspectionData
import org.example.project.data.model.InspectionListRequest
import org.example.project.domain.repository.InspectionRepository
import org.example.project.network.NetworkResult
import org.example.project.data.model.FilterContentData

data class AuditInspectionListState(
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val inspections: List<InspectionData> = emptyList(),
    val searchKey: String = "",
    val error: String? = null,
    val endReached: Boolean = false,
    val filterData: FilterContentData? = null,
    val appliedFilterState: org.example.project.data.model.AppFilterState = org.example.project.data.model.AppFilterState(),
    var errorExcel: String? = null,
    val auditItems: List<org.example.project.data.model.AuditItemContent> = emptyList(),
    val isAuditItemsLoading: Boolean = false,
    val auditItemsError: String? = null
)

class AuditInspectionListViewModel(
    private val repository: InspectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuditInspectionListState())
    val uiState: StateFlow<AuditInspectionListState> = _uiState.asStateFlow()
    
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

    fun exportToExcel() {
        viewModelScope.launch {
            _isExporting.value = true
            val currentState = _uiState.value
            val appliedFilters = currentState.appliedFilterState

            val projectIds = appliedFilters.selectedProjects.mapNotNull { it.groupId.toIntOrNull() }
            val reportedByPersons = appliedFilters.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() }

            val request = org.example.project.data.model.InspectionListRequest(
                searchKey = currentState.searchKey,
                page = 1,
                limit = 1000,
                sortType = 1,
                projectIds = if (projectIds.isNotEmpty()) projectIds else emptyList(),
                reportedByPersons = if (reportedByPersons.isNotEmpty()) reportedByPersons else emptyList(),
                openDate = formatMillis(appliedFilters.dateOpenMillis),
                endDate = formatMillis(appliedFilters.dateCloseMillis)
            )

            when (val result = repository.generateInspectionExcel(request)) {
                is NetworkResult.Success -> {
                    result.data?.excelUrl?.takeIf { it.isNotBlank() }?.let {
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

    private var currentPage = 1
    private var searchJob: Job? = null

    init {
        fetchInspections(isRefresh = true)
        fetchAuditItems()
    }
    
    private fun fetchAuditItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuditItemsLoading = true, auditItemsError = null) }
            when (val result = repository.getAuditItems()) {
                is NetworkResult.Success -> {
                    _uiState.update { 
                        it.copy(
                            isAuditItemsLoading = false,
                            auditItems = result.data.contents
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isAuditItemsLoading = false,
                            auditItemsError = result.message ?: "Failed to fetch audit items"
                        )
                    }
                }
            }
        }
    }

    /*
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
    */

    fun updateSearchKey(query: String) {
        _uiState.update { it.copy(searchKey = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // debounce
            fetchInspections(isRefresh = true)
        }
    }

    fun applyFilters(state: org.example.project.data.model.AppFilterState) {
        _uiState.update { it.copy(appliedFilterState = state) }
        fetchInspections(isRefresh = true)
    }

    fun fetchInspections(isRefresh: Boolean = false) {
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

            val request = InspectionListRequest(
                page = currentPage,
                limit = 20,
                searchKey = _uiState.value.searchKey,
                reportedByPersons = filter.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() },
                projectIds = filter.selectedProjects.mapNotNull { it.groupId.toIntOrNull() },
                openDate = formatMillis(filter.dateOpenMillis),
                endDate = formatMillis(filter.dateCloseMillis)
            )
            when (val result = repository.getInspectionList(request)) {
                is NetworkResult.Success -> {
                    val items = result.data
                    _uiState.update { currentState ->
                        if (currentPage == 1) {
                            currentState.copy(
                                isLoading = false,
                                isPaginating = false,
                                inspections = items,
                                endReached = items.isEmpty()
                            )
                        } else {
                            val existingIds = currentState.inspections.mapNotNull { it.id }.toSet()
                            val uniqueNewItems = items.filter { it.id !in existingIds }
                            
                            currentState.copy(
                                isLoading = false,
                                isPaginating = false,
                                inspections = currentState.inspections + uniqueNewItems,
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
                            isLoading = false,
                            isPaginating = false,
                            error = result.message ?: "Failed to fetch inspections"
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
}
