package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.ToolBoxTalkDetailRequest
import org.example.project.data.model.ToolBoxTalkItem
import org.example.project.domain.repository.ToolBoxTalkRepository
import org.example.project.network.NetworkResult

sealed class ToolBoxTalkDetailUiState {
    object Loading : ToolBoxTalkDetailUiState()
    data class Success(val data: ToolBoxTalkItem) : ToolBoxTalkDetailUiState()
    data class Error(val message: String) : ToolBoxTalkDetailUiState()
}

class ToolBoxTalkDetailViewModel(
    private val repository: ToolBoxTalkRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ToolBoxTalkDetailUiState>(ToolBoxTalkDetailUiState.Loading)
    val uiState: StateFlow<ToolBoxTalkDetailUiState> = _uiState.asStateFlow()

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf = _isGeneratingPdf.asStateFlow()

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl = _pdfUrl.asStateFlow()

    private val _pdfToastMessage = MutableStateFlow<String?>(null)
    val pdfToastMessage = _pdfToastMessage.asStateFlow()

    fun loadToolBoxTalkDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = ToolBoxTalkDetailUiState.Loading
            when (val result = repository.getToolBoxTalkDetail(ToolBoxTalkDetailRequest(id))) {
                is NetworkResult.Success -> {
                    _uiState.value = ToolBoxTalkDetailUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = ToolBoxTalkDetailUiState.Error(result.message ?: "Failed to load details")
                }
            }
        }
    }

    fun generatePdf(id: Int) {
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            val request = org.example.project.data.model.GenerateToolBoxTalkPdfRequest(toolBoxId = id)
            when (val result = repository.generateToolBoxTalkPdf(request)) {
                is NetworkResult.Success -> {
                    _isGeneratingPdf.value = false
                    val url = result.data.resolvedPdfUrl
                    if (!url.isNullOrBlank()) {
                        _pdfUrl.value = url
                    } else {
                        _pdfToastMessage.value = result.data.statusMessage ?: "Failed to generate PDF"
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
