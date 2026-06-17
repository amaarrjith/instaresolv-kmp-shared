package org.example.project.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.validation.LoginValidator
import org.example.project.network.NetworkResult

class LoginViewModel(
    private val repository: AuthRepository,
    private val validator: LoginValidator,
    private val authPreferences: AuthPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    var uiState = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            validator.validateEmail(_uiState.value.email)?.let { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
                return@launch
            }

            validator.validatePassword(_uiState.value.password)?.let { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
                return@launch
            }


            when (val result = repository.login(
                _uiState.value.email,
                _uiState.value.password
            )) {
                is NetworkResult.Success -> {
                    result.data.auth?.let { auth ->
                        authPreferences.saveTokens(
                            access = auth.access ?: "",
                            refresh = auth.refresh ?: "",
                            expiry = auth.tokenExpiry ?: 0L
                        )
                        authPreferences.saveLoggedInUser(result.data.user)
                        authPreferences.saveLoggedInUserInfo(result.data.userDetails)
                    }
                    authPreferences.saveLoginStatus(true)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccess = true
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
}
