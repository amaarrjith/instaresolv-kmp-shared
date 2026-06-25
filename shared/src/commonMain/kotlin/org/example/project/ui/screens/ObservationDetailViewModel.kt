package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.ObservationDetailRequest
import org.example.project.data.model.ObservationDetailResponse
import org.example.project.domain.repository.ObservationRepository
import org.example.project.network.NetworkResult

sealed class ObservationDetailUiState {
    object Idle : ObservationDetailUiState()
    object Loading : ObservationDetailUiState()
    data class Success(val detail: ObservationDetailResponse) : ObservationDetailUiState()
    data class Error(val message: String) : ObservationDetailUiState()
}

class ObservationDetailViewModel(
    private val observationRepository: ObservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ObservationDetailUiState>(ObservationDetailUiState.Idle)
    val uiState: StateFlow<ObservationDetailUiState> = _uiState

    fun loadObservationDetail(observationId: Int, notificationId: Int = -1) {
        viewModelScope.launch {
            _uiState.value = ObservationDetailUiState.Loading
            val request = ObservationDetailRequest(
                observationId = observationId,
                notificationId = notificationId
            )
            when (val result = observationRepository.getObservationDetail(request)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    _uiState.value = ObservationDetailUiState.Success(response)
                }
                is NetworkResult.Error -> {
                    _uiState.value = ObservationDetailUiState.Error(result.message ?: "Failed to load observation details")
                }
            }
        }
    }
}
