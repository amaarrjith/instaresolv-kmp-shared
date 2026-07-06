package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.data.model.LessonLearnedDetailRequest
import org.example.project.data.model.LessonLearnedDetailResponseData
import org.example.project.data.model.GenerateLessonLearnedPdfRequest
import org.example.project.domain.repository.LessonLearnedRepository
import org.example.project.network.NetworkResult

sealed class LessonsLearnedDetailUiState {
    object Loading : LessonsLearnedDetailUiState()
    data class Success(val data: LessonLearnedDetailResponseData) : LessonsLearnedDetailUiState()
    data class Error(val message: String) : LessonsLearnedDetailUiState()
}

class LessonsLearnedDetailViewModel(
    private val repository: LessonLearnedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LessonsLearnedDetailUiState>(LessonsLearnedDetailUiState.Loading)
    val uiState: StateFlow<LessonsLearnedDetailUiState> = _uiState.asStateFlow()

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf.asStateFlow()

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl: StateFlow<String?> = _pdfUrl.asStateFlow()

    private val _pdfToastMessage = MutableStateFlow<String?>(null)
    val pdfToastMessage: StateFlow<String?> = _pdfToastMessage.asStateFlow()

    fun loadLessonLearnedDetail(id: Int) {
        viewModelScope.launch {
            _uiState.value = LessonsLearnedDetailUiState.Loading
            when (val result = repository.getLessonLearnedDetail(LessonLearnedDetailRequest(id))) {
                is NetworkResult.Success -> {
                    _uiState.value = LessonsLearnedDetailUiState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = LessonsLearnedDetailUiState.Error(result.message)
                }
            }
        }
    }

    fun generatePdf(id: Int) {
        viewModelScope.launch {
            _isGeneratingPdf.value = true
            when (val result = repository.generateLessonLearnedPdf(GenerateLessonLearnedPdfRequest(id))) {
                is NetworkResult.Success -> {
                    val url = result.data.pdfUrl ?: result.data.excelUrl
                    if (!url.isNullOrBlank()) {
                        _pdfUrl.value = url
                    } else {
                        _pdfToastMessage.value = "PDF URL not found"
                    }
                }
                is NetworkResult.Error -> {
                    _pdfToastMessage.value = result.message ?: "Failed to generate PDF"
                }
            }
            _isGeneratingPdf.value = false
        }
    }

    fun clearPdfUrl() {
        _pdfUrl.value = null
    }

    fun clearPdfToastMessage() {
        _pdfToastMessage.value = null
    }

    fun setPdfToastMessage(message: String) {
        _pdfToastMessage.value = message
    }
}
