package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.InspectionDetailRequest
import org.example.project.data.model.InspectionDetailResponse
import org.example.project.domain.repository.InspectionRepository
import org.example.project.network.NetworkResult

sealed class InspectionDetailUiState {
    data object Idle : InspectionDetailUiState()
    data object Loading : InspectionDetailUiState()
    data class Success(val detail: InspectionDetailResponse) : InspectionDetailUiState()
    data class Error(val message: String) : InspectionDetailUiState()
}

class InspectionDetailViewModel(
    private val inspectionRepository: InspectionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<InspectionDetailUiState>(InspectionDetailUiState.Idle)
    val uiState: StateFlow<InspectionDetailUiState> = _uiState.asStateFlow()

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf = _isGeneratingPdf.asStateFlow()

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl = _pdfUrl.asStateFlow()

    private val _pdfToastMessage = MutableStateFlow<String?>(null)
    val pdfToastMessage = _pdfToastMessage.asStateFlow()

    fun loadInspectionDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = InspectionDetailUiState.Loading
            val request = InspectionDetailRequest(id = id)
            val result = inspectionRepository.getInspectionDetail(request)
            
            when (result) {
                is NetworkResult.Success -> {
                    if (result.data != null) {
                        _uiState.value = InspectionDetailUiState.Success(result.data)
                    } else {
                        _uiState.value = InspectionDetailUiState.Error("Details not found")
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = InspectionDetailUiState.Error(result.message ?: "Failed to load details")
                }
            }
        }
    }

    fun generatePdf(id: Int) {
        // Mocking PDF generation for now, since it wasn't specified in the API requirements.
        // It can be added later if there is an inspection/pdf API.
    }

    fun setPdfToastMessage(message: String) {
        _pdfToastMessage.update { message }
    }

    fun clearPdfToastMessage() {
        _pdfToastMessage.update { null }
    }

    fun clearPdfUrl() {
        _pdfUrl.update { null }
    }
}
