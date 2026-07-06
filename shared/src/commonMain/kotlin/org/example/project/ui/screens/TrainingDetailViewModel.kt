package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.TrainingDetailData
import org.example.project.data.model.TrainingDetailRequest
import org.example.project.data.model.TrainingVideoUrlRequest
import org.example.project.data.model.TrainingVideoUrlData
import org.example.project.domain.repository.TrainingRepository
import org.example.project.network.NetworkResult

data class TrainingDetailState(
    val isLoading: Boolean = false,
    val detailData: TrainingDetailData? = null,
    val error: String? = null,
    val isVideoLoading: Boolean = false,
    val videoData: TrainingVideoUrlData? = null,
    val videoError: String? = null
)

class TrainingDetailViewModel(
    private val repository: TrainingRepository,
    private val trainingId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingDetailState())
    val uiState: StateFlow<TrainingDetailState> = _uiState.asStateFlow()

    init {
        loadTrainingDetail()
    }

    fun loadTrainingDetail() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            val request = TrainingDetailRequest(id = trainingId)
            when (val result = repository.getTrainingDetail(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            detailData = result.data
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

    fun loadTrainingVideoUrl(onSuccess: (TrainingVideoUrlData) -> Unit) {
        _uiState.update { it.copy(isVideoLoading = true, videoError = null) }
        viewModelScope.launch {
            val request = TrainingVideoUrlRequest(id = trainingId)
            when (val result = repository.getTrainingVideoUrl(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isVideoLoading = false,
                            videoData = result.data
                        )
                    }
                    onSuccess(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isVideoLoading = false,
                            videoError = result.message
                        )
                    }
                }
            }
        }
    }
}
