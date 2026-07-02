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
import org.example.project.data.model.LessonLearnedData
import org.example.project.data.model.LessonLearnedListRequest
import org.example.project.domain.repository.LessonLearnedRepository
import org.example.project.network.NetworkResult
import org.example.project.data.model.AppFilterState

data class LessonsLearnedListState(
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val lessons: List<LessonLearnedData> = emptyList(),
    val searchKey: String = "",
    val error: String? = null,
    val endReached: Boolean = false,
    val appliedFilterState: AppFilterState = AppFilterState()
)

class LessonsLearnedListViewModel(
    private val repository: LessonLearnedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonsLearnedListState())
    val uiState: StateFlow<LessonsLearnedListState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var searchJob: Job? = null

    init {
        fetchLessonsLearned(isRefresh = true)
    }

    fun updateSearchKey(query: String) {
        _uiState.update { it.copy(searchKey = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // debounce
            fetchLessonsLearned(isRefresh = true)
        }
    }

    fun applyFilters(state: AppFilterState) {
        _uiState.update { it.copy(appliedFilterState = state) }
        fetchLessonsLearned(isRefresh = true)
    }

    fun fetchLessonsLearned(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
        }
        
        if (_uiState.value.isLoading || _uiState.value.isPaginating) return
        if (!isRefresh && _uiState.value.endReached) return

        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            } else {
                _uiState.update { it.copy(isPaginating = true, error = null) }
            }

            val request = LessonLearnedListRequest(
                searchKey = _uiState.value.searchKey.takeIf { it.isNotBlank() },
                pageNumber = currentPage,
                limit = 20,
                sortType = _uiState.value.appliedFilterState.sortType,
                projectIds = _uiState.value.appliedFilterState.selectedProjectIds.toList().takeIf { it.isNotEmpty() },
                openDate = _uiState.value.appliedFilterState.fromDate?.let { formatDate(it) },
                endDate = _uiState.value.appliedFilterState.toDate?.let { formatDate(it) },
                reportedByPersons = _uiState.value.appliedFilterState.selectedUserId?.let { listOf(it) }
            )

            when (val result = repository.getLessonsLearnedList(request)) {
                is NetworkResult.Success -> {
                    val newItems = result.data
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isPaginating = false,
                            lessons = if (isRefresh) newItems else state.lessons + newItems,
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
    
    private fun formatDate(millis: Long): String {
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
        val tz = kotlinx.datetime.TimeZone.currentSystemDefault()
        val dt = instant.toLocalDateTime(tz)
        return "${dt.dayOfMonth.toString().padStart(2, '0')}-${dt.monthNumber.toString().padStart(2, '0')}-${dt.year}"
    }
}
