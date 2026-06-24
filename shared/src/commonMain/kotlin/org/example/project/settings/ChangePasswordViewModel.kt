package org.example.project.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult

sealed class ChangePasswordUiState {
    object Idle : ChangePasswordUiState()
    object Loading : ChangePasswordUiState()
    data class Success(val message: String) : ChangePasswordUiState()
    data class Error(val message: String, val errorCode: Int? = null) : ChangePasswordUiState()
}

class ChangePasswordViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChangePasswordUiState>(ChangePasswordUiState.Idle)
    val uiState: StateFlow<ChangePasswordUiState> = _uiState

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.value = ChangePasswordUiState.Loading
            when (val result = repository.changePassword(oldPassword, newPassword)) {
                is NetworkResult.Success -> {
                    _uiState.value = ChangePasswordUiState.Success(
                        result.data.statusMessage.takeIf { it?.isNotEmpty() == true } ?: "Password changed"
                    )
                }
                is NetworkResult.Error -> {
                    val msg = if (result.errorCode == 1002) "Incorrect old password" else result.message
                    _uiState.value = ChangePasswordUiState.Error(msg, result.errorCode)
                }
            }
        }
    }
    
    fun resetState() {
        _uiState.value = ChangePasswordUiState.Idle
    }
}
