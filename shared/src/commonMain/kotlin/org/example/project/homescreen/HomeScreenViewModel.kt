package org.example.project.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.ObservationRepository
import org.example.project.domain.repository.PermitRepository
import org.example.project.network.NetworkResult

class HomeScreenViewModel(
    private val repository: AuthRepository,
    private val preferences: AuthPreferences,
    private val permitRepository: PermitRepository,
    private val observationRepository: ObservationRepository
): ViewModel()  {
    val user = preferences.getLoggedInUser()
    val userInfo = preferences.getLoggedInUserInfo()

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl: StateFlow<String?> = _pdfUrl

    private val _pdfToastMessage = MutableStateFlow<String?>(null)
    val pdfToastMessage: StateFlow<String?> = _pdfToastMessage
    private val _pdfErrorToastMessage = MutableStateFlow<String?>(null)
    val pdfErrorToastMessage: StateFlow<String?> = _pdfErrorToastMessage

    private val _pdfModuleType = MutableStateFlow("Observation")
    val pdfModuleType: StateFlow<String> = _pdfModuleType

    fun generatePermitPDF(permitId: Int) {
        viewModelScope.launch {
            _pdfModuleType.value = "Permit"
            _isGeneratingPdf.value = true
            val request = org.example.project.data.model.GeneratePermitPdfRequest(permitId = permitId)
            when (val result = permitRepository.generatePermitPdf(request)) {
                is NetworkResult.Success -> {
                    _isGeneratingPdf.value = false
                    val url = result.data.pdfUrl ?: result.data.excelUrl
                    if (!url.isNullOrBlank()) {
                        _pdfUrl.value = url
                    } else {
                        _pdfToastMessage.value = result.data.statusMessage ?: "Failed to generate Permit PDF"
                    }
                }
                is NetworkResult.Error -> {
                    _isGeneratingPdf.value = false
                    _pdfErrorToastMessage.value = result.message ?: "Failed to generate Permit PDF"
                }
            }
        }
    }

    fun generatePdf(observationId: Int) {
        viewModelScope.launch {
            _pdfModuleType.value = "Observation"
            _isGeneratingPdf.value = true
            val request = org.example.project.data.model.GenerateObservationPdfRequest(observationId = observationId)
            when (val result = observationRepository.generatePdf(request)) {
                is NetworkResult.Success -> {
                    _isGeneratingPdf.value = false
                    val url = result.data.pdfUrl ?: result.data.excelUrl
                    if (!url.isNullOrBlank()) {
                        _pdfUrl.value = url
                    } else {
                        _pdfToastMessage.value = result.data.statusMessage ?: "Failed to generate PDF"
                    }
                }
                is NetworkResult.Error -> {
                    _isGeneratingPdf.value = false
                    _pdfErrorToastMessage.value = result.message ?: "Failed to generate PDF"
                }
            }
        }
    }

    fun setPdfToastMessage(message: String) {
        _pdfToastMessage.value = message
    }

    fun setPdfErrorToastMessage(message: String) {
        _pdfErrorToastMessage.value = message
    }

    fun clearToasts() {
        _pdfToastMessage.value = null
        _pdfErrorToastMessage.value = null
    }

    fun clearPdfUrl() {
        _pdfUrl.value = null
    }
}