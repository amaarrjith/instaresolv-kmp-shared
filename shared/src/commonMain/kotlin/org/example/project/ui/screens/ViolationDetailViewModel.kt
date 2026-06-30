package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.ViolationData
import org.example.project.data.model.ViolationDetailRequest
import org.example.project.domain.repository.ViolationRepository
import org.example.project.network.NetworkResult

data class ViolationDetailState(
    val isLoading: Boolean = false,
    val detail: ViolationData? = null,
    val error: String? = null
)

class ViolationDetailViewModel(
    private val repository: ViolationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ViolationDetailState())
    val uiState: StateFlow<ViolationDetailState> = _uiState.asStateFlow()

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl: StateFlow<String?> = _pdfUrl

    private val _pdfToastMessage = MutableStateFlow<String?>(null)
    val pdfToastMessage: StateFlow<String?> = _pdfToastMessage

    fun loadViolationDetail(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val request = ViolationDetailRequest(id = id)
            when (val result = repository.getViolationDetail(request)) {
                is NetworkResult.Success -> {
                    val data = result.data
                    if (data != null) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                detail = data
                            )
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = "Failed to load violation details"
                            )
                        }
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = result.message ?: "Failed to load violation details"
                        )
                    }
                }
            }
        }
    }

    fun generatePdf(violationId: Int) {
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            val request = org.example.project.data.model.GenerateViolationPdfRequest(violationId = violationId)
            when (val result = repository.generatePdf(request)) {
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
