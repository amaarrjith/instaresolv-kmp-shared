package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.TrainingData
import org.example.project.data.model.TrainingListRequest
import org.example.project.domain.repository.TrainingRepository
import org.example.project.network.NetworkResult

data class TrainingListState(
    val isLoading: Boolean = false,
    val isPaginating: Boolean = false,
    val trainings: List<TrainingData> = emptyList(),
    val error: String? = null,
    val isLastPage: Boolean = false
)

class TrainingListViewModel(
    private val repository: TrainingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingListState())
    val uiState: StateFlow<TrainingListState> = _uiState.asStateFlow()

    private var currentPage = 1
    private val limit = 20

    init {
        loadTrainings(isRefresh = true)
    }

    fun loadTrainings(isRefresh: Boolean = false) {
        if (isRefresh) {
            currentPage = 1
            _uiState.update { it.copy(isLoading = true, error = null, isLastPage = false) }
        } else {
            if (_uiState.value.isLastPage || _uiState.value.isPaginating || _uiState.value.isLoading) return
            _uiState.update { it.copy(isPaginating = true, error = null) }
        }

        viewModelScope.launch {
            val request = TrainingListRequest(
                pageNumber = currentPage,
                limit = limit
            )

            when (val result = repository.getMyTrainingList(request)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    val newTrainings = response.trainings
                    val isLastPageReached = newTrainings.size < limit

                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isPaginating = false,
                            trainings = if (isRefresh) newTrainings else state.trainings + newTrainings,
                            isLastPage = isLastPageReached
                        )
                    }
                    if (newTrainings.isNotEmpty()) {
                        currentPage++
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isPaginating = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
