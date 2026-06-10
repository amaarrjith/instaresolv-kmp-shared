package org.example.project.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.project.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            loginUseCase(
                _uiState.value.email,
                _uiState.value.password
            )
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccess = true
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
        }
    }
}
