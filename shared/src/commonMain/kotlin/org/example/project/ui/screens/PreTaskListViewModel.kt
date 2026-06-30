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

data class PreTaskListState(
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val preTasks: List<PreTaskData> = emptyList(),
    val searchKey: String = "",
    val error: String? = null,
    val endReached: Boolean = false,
    val filterData: FilterContentData? = null,
    val appliedFilterState: org.example.project.data.model.AppFilterState = org.example.project.data.model.AppFilterState()
)

class PreTaskListViewModel(
    private val repository: PreTaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreTaskListState())
    val uiState: StateFlow<PreTaskListState> = _uiState.asStateFlow()

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

    fun fetchPreTasks(isRefresh: Boolean = false) {
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

            val request = PreTaskListRequest(
                pageNumber = currentPage,
                limit = 20,
                searchKey = _uiState.value.searchKey,
                reportedByPersons = filter.selectedReportedBy.mapNotNull { it.userId.toIntOrNull() },
                projectIds = filter.selectedProjects.mapNotNull { it.groupId.toIntOrNull() },
                openDate = formatMillis(filter.dateOpenMillis),
                endDate = formatMillis(filter.dateCloseMillis)
            )
            when (val result = repository.getPreTaskList(request)) {
                is NetworkResult.Success -> {
                    val items = result.data
                    _uiState.update { currentState ->
                        if (currentPage == 1) {
                            currentState.copy(
                                isLoading = false,
                                isPaginating = false,
                                preTasks = items,
                                endReached = items.isEmpty()
                            )
                        } else {
                            val existingIds = currentState.preTasks.mapNotNull { it.id }.toSet()
                            val uniqueNewItems = items.filter { it.id !in existingIds }
                            
                            currentState.copy(
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
}
