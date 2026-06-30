package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.IncidentDetailRequest
import org.example.project.data.model.IncidentDetailResponse
import org.example.project.domain.repository.IncidentRepository
import org.example.project.network.NetworkResult

sealed class IncidentDetailUiState {
    object Idle : IncidentDetailUiState()
    object Loading : IncidentDetailUiState()
    data class Success(val detail: IncidentDetailResponse) : IncidentDetailUiState()
    data class Error(val message: String) : IncidentDetailUiState()
}

class IncidentDetailViewModel(
    private val incidentRepository: IncidentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<IncidentDetailUiState>(IncidentDetailUiState.Idle)
    val uiState: StateFlow<IncidentDetailUiState> = _uiState

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl: StateFlow<String?> = _pdfUrl

    private val _pdfToastMessage = MutableStateFlow<String?>(null)
    val pdfToastMessage: StateFlow<String?> = _pdfToastMessage

    fun loadIncidentDetail(incidentId: Int) {
        viewModelScope.launch {
            _uiState.value = IncidentDetailUiState.Loading
            val request = IncidentDetailRequest(id = incidentId)
            when (val result = incidentRepository.getIncidentDetail(request)) {
                is NetworkResult.Success -> {
                    val response = result.data
                    if (response != null) {
                        _uiState.value = IncidentDetailUiState.Success(response)
                    } else {
                        _uiState.value = IncidentDetailUiState.Error("Invalid response")
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = IncidentDetailUiState.Error(result.message ?: "Failed to load incident details")
                }
            }
        }
    }

    fun generatePdf(incidentId: Int) {
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            val request = org.example.project.data.model.GenerateIncidentPdfRequest(incidentId = incidentId)
            when (val result = incidentRepository.generatePdf(request)) {
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
