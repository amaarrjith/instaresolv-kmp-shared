package org.example.project.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.NetworkResult
import org.example.project.domain.validation.LoginValidator

class LoginViewModel(
    private val repository: AuthRepository,
    private val validator: LoginValidator
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

            validator.validateEmail(uiState.email)?.let {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = it
                )
                return@launch
            }

            validator.validatePassword(uiState.password)?.let {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = it
                )
                return@launch
            }

            try {
                when (
                    repository.login(
                        email = uiState.email,
                        password = uiState.password
                    )
                ) {
                    is NetworkResult.Success -> {
                        uiState = uiState.copy(
                            isLoading = false,
                            isLoginSuccess = true
                        )
                    }

                    is NetworkResult.Error -> {
                        uiState = uiState.copy(
                            isLoading = false,
                            isLoginSuccess = false
                        )
                    }
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    isLoginSuccess = false,
                    errorMessage = e.message ?: "Something went wrong"
                )
            }
        }
    }
}
