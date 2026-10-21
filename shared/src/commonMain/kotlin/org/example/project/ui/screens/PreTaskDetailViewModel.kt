package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.PreTaskDetailRequest
import org.example.project.data.model.PreTaskDetailResponseData
import org.example.project.domain.repository.PreTaskRepository
import org.example.project.network.NetworkResult
import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.getString

sealed class PreTaskDetailUiState {
    object Loading : PreTaskDetailUiState()
    data class Success(val data: PreTaskDetailResponseData) : PreTaskDetailUiState()
    data class Error(val message: String) : PreTaskDetailUiState()
}

class PreTaskDetailViewModel(
    private val repository: PreTaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PreTaskDetailUiState>(PreTaskDetailUiState.Loading)
    val uiState: StateFlow<PreTaskDetailUiState> = _uiState.asStateFlow()

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf = _isGeneratingPdf.asStateFlow()

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl = _pdfUrl.asStateFlow()

    private val _pdfToastMessage = MutableStateFlow<String?>(null)
    val pdfToastMessage = _pdfToastMessage.asStateFlow()

    fun loadPreTaskDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = PreTaskDetailUiState.Loading
            when (val result = repository.getPreTaskDetail(PreTaskDetailRequest(id))) {
                is NetworkResult.Success -> {
                    _uiState.value = PreTaskDetailUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = PreTaskDetailUiState.Error(result.message)
                }
            }
        }
    }

    fun generatePdf(id: Int) {
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            val request = org.example.project.data.model.GeneratePreTaskPdfRequest(id = id)
            when (val result = repository.generatePreTaskPdf(request)) {
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
