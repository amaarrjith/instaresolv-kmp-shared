package org.example.project.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult

sealed class GeneralContentsUiState {
    object Idle : GeneralContentsUiState()
    object Loading : GeneralContentsUiState()
    data class Success(val content: String) : GeneralContentsUiState()
    data class Error(val message: String) : GeneralContentsUiState()
}

class GeneralContentsViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GeneralContentsUiState>(GeneralContentsUiState.Idle)
    val uiState: StateFlow<GeneralContentsUiState> = _uiState.asStateFlow()

    fun fetchContent(type: Int) {
        viewModelScope.launch {
            _uiState.value = GeneralContentsUiState.Loading
            when (val result = authRepository.getGeneralContents(type)) {
                is NetworkResult.Success -> {
                    val content = result.data.content ?: ""
                    _uiState.value = GeneralContentsUiState.Success(content)
                }
                is NetworkResult.Error -> {
                    _uiState.value = GeneralContentsUiState.Error(result.message ?: "Unknown Error")
                }
            }
        }
    }
}
