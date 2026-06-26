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
    val appliedFilterState: AppFilterState = AppFilterState()
)

class IncidentListViewModel(
    private val repository: IncidentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncidentListState())
    val uiState: StateFlow<IncidentListState> = _uiState.asStateFlow()

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
