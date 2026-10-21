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
import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.getString

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
                        _uiState.value = InspectionDetailUiState.Error(getString(Res.string.error_details_not_found))
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = InspectionDetailUiState.Error(result.message ?: getString(Res.string.error_failed_to_load_details))
                }
            }
        }
    }

    fun generatePdf(id: Int) {
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            val request = org.example.project.data.model.GenerateInspectionPdfRequest(id = id)
            when (val result = inspectionRepository.generateInspectionPdf(request)) {
                is NetworkResult.Success -> {
                    _isGeneratingPdf.value = false
                    val url = result.data?.resolvedPdfUrl
                    if (!url.isNullOrBlank()) {
                        _pdfUrl.value = url
                    } else {
                        _pdfToastMessage.value = result.data?.statusMessage ?: getString(Res.string.error_failed_to_generate_pdf)
                    }
                }
                is NetworkResult.Error -> {
                    _isGeneratingPdf.value = false
                    _pdfToastMessage.value = result.message ?: getString(Res.string.error_failed_to_generate_pdf)
                }
            }
        }
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
