package org.example.project.ui.screens

import kotlinx.datetime.toLocalDateTime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.IncidentData
import org.example.project.data.model.IncidentRequest
import org.example.project.domain.repository.IncidentRepository
import org.example.project.network.NetworkResult
import org.example.project.data.model.FilterContentData
import org.example.project.data.model.AppFilterState

data class IncidentListState(
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val incidents: List<IncidentData> = emptyList(),
    val searchKey: String = "",
    val error: String? = null,
    val isLastPage: Boolean = false,
    val filterData: FilterContentData? = null,
    val appliedFilterState: AppFilterState = AppFilterState(),
    var errorExcel: String? = null
) {
}

class IncidentListViewModel(
    private val repository: IncidentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncidentListState())
    val uiState: StateFlow<IncidentListState> = _uiState.asStateFlow()

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
    private val limit = 20

    init {
        loadIncidents(isRefresh = true)
        fetchFilterContent()
    }

    private fun fetchFilterContent() {
        viewModelScope.launch {
            when (val result = repository.getFilterContent()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(filterData = result.data) }
                }
                is NetworkResult.Error -> {
                    // Handle filter loading error if necessary
                }
            }
        }
    }

    fun applyFilters(filterState: AppFilterState) {
        _uiState.update { it.copy(appliedFilterState = filterState) }
        loadIncidents(isRefresh = true)
    }

    fun updateSearchKey(searchKey: String) {
        _uiState.update { it.copy(searchKey = searchKey) }
        loadIncidents(isRefresh = true)
    }

    fun loadIncidents(isRefresh: Boolean = false, searchKey: String? = null) {
        if (searchKey != null) {
            _uiState.update { it.copy(searchKey = searchKey) }
        }

        if (isRefresh) {
            currentPage = 1
            _uiState.update { it.copy(isLoading = true, error = null, isLastPage = false) }
        } else {
            if (_uiState.value.isLastPage || _uiState.value.isPaginating) return
            _uiState.update { it.copy(isPaginating = true, error = null) }
        }

        viewModelScope.launch {
            val currentState = _uiState.value
            val appliedFilters = currentState.appliedFilterState

            // Filter fields mapped to API request
            val projectIds = appliedFilters.selectedProjects.mapNotNull { it.groupId.toIntOrNull() }
            val reportedByPersons = appliedFilters.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() }
            
            // Note: IncidentType filtering is now supported by AppFilterBottomSheet
            val incidentTypes: List<Int>? = appliedFilters.selectedIncidentTypes

            val request = IncidentRequest(
                searchKey = currentState.searchKey,
                pageNumber = currentPage,
                limit = limit,
                sortType = 1,
                projectIds = if (projectIds.isNotEmpty()) projectIds else null,
                reportedByPersons = if (reportedByPersons.isNotEmpty()) reportedByPersons else null,
                incidentTypes = if (incidentTypes?.isNotEmpty() == true) incidentTypes else null,
                openDate = formatMillis(appliedFilters.dateOpenMillis),
                endDate = formatMillis(appliedFilters.dateCloseMillis)
            )
            
            when (val result = repository.getIncidentList(request)) {
                is NetworkResult.Success -> {
                    val newIncidents = result.data ?: emptyList()
                    val isLastPageReached = newIncidents.size < limit
                    
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isPaginating = false,
                            incidents = if (isRefresh) newIncidents else state.incidents + newIncidents,
                            isLastPage = isLastPageReached
                        )
                    }
                    if (newIncidents.isNotEmpty()) {
                        currentPage++
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isPaginating = false,
                            error = result.message ?: "Failed to load incidents"
                        )
                    }
                }
            }
        }
    }

    fun exportToExcel() {
        viewModelScope.launch {
            _isExporting.value = true
            val currentState = _uiState.value
            val appliedFilters = currentState.appliedFilterState

            val projectIds = appliedFilters.selectedProjects.mapNotNull { it.groupId.toIntOrNull() }
            val reportedByPersons = appliedFilters.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() }
            val incidentTypes: List<Int>? = appliedFilters.selectedIncidentTypes

            val request = IncidentRequest(
                searchKey = currentState.searchKey,
                pageNumber = 1,
                limit = 1000, // Export usually doesn't need pagination, or limit could be large
                sortType = 1,
                projectIds = if (projectIds.isNotEmpty()) projectIds else null,
                reportedByPersons = if (reportedByPersons.isNotEmpty()) reportedByPersons else null,
                incidentTypes = if (incidentTypes?.isNotEmpty() == true) incidentTypes else null,
                openDate = formatMillis(appliedFilters.dateOpenMillis),
                endDate = formatMillis(appliedFilters.dateCloseMillis)
            )

            when (val result = repository.generateIncidentExcel(request)) {
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
