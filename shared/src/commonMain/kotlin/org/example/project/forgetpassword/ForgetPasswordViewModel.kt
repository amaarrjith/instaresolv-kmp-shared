package org.example.project.forgetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult
import org.example.project.domain.validation.LoginValidator

class ForgetPasswordViewModel(
    private val repository: AuthRepository,
    private val validator: LoginValidator,
): ViewModel() {
    private val _uiState = MutableStateFlow(ForgetPasswordUiState())
    var uiState = _uiState.asStateFlow()

    fun forgetPassword(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            validator.validateEmail(email)?.let { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
                return@launch
            }
            when(val result = repository.forgetPassword(email)) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isPasswordResetSuccess = true,
                            successMessage = result.data.message
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(isPasswordResetSuccess = false) }
    }
}