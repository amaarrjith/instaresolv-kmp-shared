package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.PermitContentItem
import org.example.project.data.model.PermitContentRequest
import org.example.project.domain.repository.PermitRepository
import org.example.project.network.NetworkResult

data class CreatePermitUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val certificateValidity: List<PermitContentItem> = emptyList(),
    val generalConditions: List<PermitContentItem> = emptyList(),
    val certificateValidityAnswers: Map<Int, String> = emptyMap(),
    val generalConditionAnswers: Map<Int, String> = emptyMap(),
    val signatureUrl: String? = null
)

class CreatePermitViewModel(
    private val permitRepository: PermitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePermitUiState())
    val uiState: StateFlow<CreatePermitUiState> = _uiState.asStateFlow()

    fun fetchPermitContents(permitTypeId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val request = PermitContentRequest(id = permitTypeId)
            when (val result = permitRepository.getPermitContents(request)) {
                is NetworkResult.Success -> {
                    val data = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            certificateValidity = data?.certificateValidity ?: emptyList(),
                            generalConditions = data?.generalConditions ?: emptyList()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun updateCertificateValidity(id: Int, value: String) {
        _uiState.update {
            it.copy(
                certificateValidityAnswers = it.certificateValidityAnswers.toMutableMap().apply {
                    put(id, value)
                }
            )
        }
    }

    fun updateGeneralCondition(id: Int, answer: String) {
        _uiState.update {
            it.copy(
                generalConditionAnswers = it.generalConditionAnswers.toMutableMap().apply {
                    put(id, answer)
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun updateSignatureUrl(url: String?) {
        _uiState.update { it.copy(signatureUrl = url) }
    }
}
