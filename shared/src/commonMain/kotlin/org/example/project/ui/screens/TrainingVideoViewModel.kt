package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.TrainingVideoUrlData
import org.example.project.data.model.TrainingVideoUrlRequest
import org.example.project.domain.repository.TrainingRepository
import org.example.project.network.NetworkResult

data class TrainingVideoState(
    val isLoading: Boolean = false,
    val videoData: TrainingVideoUrlData? = null,
    val error: String? = null
)

class TrainingVideoViewModel(
    private val repository: TrainingRepository,
    private val trainingId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingVideoState())
    val uiState: StateFlow<TrainingVideoState> = _uiState.asStateFlow()

    init {
        loadTrainingVideoUrl()
    }

    fun loadTrainingVideoUrl() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val request = TrainingVideoUrlRequest(id = trainingId)
            when (val result = repository.getTrainingVideoUrl(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            videoData = result.data
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
