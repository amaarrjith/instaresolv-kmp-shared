package org.example.project.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.project.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    fun updateEmail(value: String) {
        uiState = uiState.copy(email = value)
    }

    fun updatePassword(value: String) {
        uiState = uiState.copy(password = value)
    }

    fun login() {
        viewModelScope.launch {

            uiState = uiState.copy(isLoading = true)

            val result = loginUseCase(uiState.email, uiState.password)

            result
                .onSuccess {
                    uiState = uiState.copy(isLoading = false, isLoginSuccess = true)
                }
                .onFailure {
                    uiState = uiState.copy(isLoading = false, errorMessage = it.message)
                }
        }
    }
}
