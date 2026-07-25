package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.PendingActionItem
import org.example.project.data.model.PermitPendingActionItem
import org.example.project.domain.repository.PendingActionRepository
import org.example.project.domain.repository.PermitRepository
import org.example.project.network.NetworkResult

data class PendingActionListState(
    val isLoading: Boolean = false,
    val pendingActions: List<PendingActionItem> = emptyList(),
    val error: String? = null,
    // Permit tab state
    val isPermitLoading: Boolean = false,
    val permitPendingActions: List<PermitPendingActionItem> = emptyList(),
    val permitError: String? = null
)

class PendingActionListViewModel(
    private val repository: PendingActionRepository,
    private val permitRepository: PermitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PendingActionListState())
    val uiState: StateFlow<PendingActionListState> = _uiState.asStateFlow()

    private val _isGeneratingPdf = MutableStateFlow(false)
    val isGeneratingPdf: StateFlow<Boolean> = _isGeneratingPdf

    private val _pdfUrl = MutableStateFlow<String?>(null)
    val pdfUrl: StateFlow<String?> = _pdfUrl

    private val _pdfToastMessage = MutableStateFlow<String?>(null)
    val pdfToastMessage: StateFlow<String?> = _pdfToastMessage

    private val _pdfErrorToastMessage = MutableStateFlow<String?>(null)
    val pdfErrorToastMessage: StateFlow<String?> = _pdfErrorToastMessage

    fun generatePermitPDF(permitId: Int) {
        viewModelScope.launch {
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

    init {
        fetchPendingActions()
        fetchPermitPendingActions()
    }

    fun fetchPendingActions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getPendingActions()) {
                is NetworkResult.Success -> {
                    val actions = result.data.pendingActions
                    _uiState.update { 
                        it.copy(isLoading = false, pendingActions = actions) 
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { 
                        it.copy(isLoading = false, error = result.message ?: "Failed to fetch pending actions") 
                    }
                }
            }
        }
    }

    fun fetchPermitPendingActions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPermitLoading = true, permitError = null) }
            when (val result = permitRepository.getPermitPendingActions()) {
                is NetworkResult.Success -> {
                    val items = result.data.results ?: emptyList()
                    _uiState.update {
                        it.copy(isPermitLoading = false, permitPendingActions = items)
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(isPermitLoading = false, permitError = result.message ?: "Failed to fetch permit pending actions")
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearPermitError() {
        _uiState.update { it.copy(permitError = null) }
    }
}
