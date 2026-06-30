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

sealed class CloseObservationState {
    object Idle : CloseObservationState()
    object Loading : CloseObservationState()
    object Success : CloseObservationState()
    data class Error(val message: String) : CloseObservationState()
}

class ObservationDetailViewModel(
    private val observationRepository: ObservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ObservationDetailUiState>(ObservationDetailUiState.Idle)
    val uiState: StateFlow<ObservationDetailUiState> = _uiState

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl: StateFlow<String?> = _pdfUrl

    private val _pdfToastMessage = MutableStateFlow<String?>(null)
    val pdfToastMessage: StateFlow<String?> = _pdfToastMessage

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

    private val _closeUiState = MutableStateFlow<CloseObservationState>(CloseObservationState.Idle)
    val closeUiState: StateFlow<CloseObservationState> = _closeUiState

    fun closeObservation(observationId: Int, description: String, images: List<org.example.project.ui.screens.ObservationImage>) {
        viewModelScope.launch {
            _closeUiState.value = CloseObservationState.Loading
            
            val imageDescriptions = images.filter { it.imageUrl?.isNotEmpty() == true }.map {
                org.example.project.data.model.ObservationImageDescription(
                    image = it.imageUrl,
                    description = it.description
                )
            }
            
            val request = org.example.project.data.model.CloseObservationRequest(
                observationId = observationId,
                description = description,
                imageDescription = imageDescriptions
            )
            
            when (val result = observationRepository.closeObservation(request)) {
                is NetworkResult.Success -> {
                    _closeUiState.value = CloseObservationState.Success
                }
                is NetworkResult.Error -> {
                    _closeUiState.value = CloseObservationState.Error(result.message ?: "Failed to close observation")
                }
            }
        }
    }
    
    fun resetCloseState() {
        _closeUiState.value = CloseObservationState.Idle
    }

    fun generatePdf(observationId: Int) {
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            val request = org.example.project.data.model.GenerateObservationPdfRequest(observationId = observationId)
            when (val result = observationRepository.generatePdf(request)) {
                is NetworkResult.Success -> {
                    _isGeneratingPdf.value = false
                    val url = result.data?.pdfUrl ?: result.data?.excelUrl
                    if (!url.isNullOrBlank()) {
                        _pdfUrl.value = url
                    } else {
                        _pdfToastMessage.value = result.data?.statusMessage ?: "Failed to generate PDF"
                    }
                }
                is NetworkResult.Error -> {
                    _isGeneratingPdf.value = false
                    _pdfToastMessage.value = result.message ?: "Failed to generate PDF"
                }
            }
        }
    }

    fun clearPdfUrl() {
        _pdfUrl.value = null
    }

    fun setPdfToastMessage(message: String) {
        _pdfToastMessage.value = message
    }

    fun clearPdfToastMessage() {
        _pdfToastMessage.value = null
    }
}
