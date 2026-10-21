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
import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.getString

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

            val email = _uiState.value.email.trim()
            val password = _uiState.value.password.trim()

            validator.validateEmail(email)?.let { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
                return@launch
            }

            validator.validatePassword(password)?.let { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
                return@launch
            }


            when (val result = repository.login(
                email,
                password
            )) {
                is NetworkResult.Success -> {
                    val auth = result.data.auth
                    if (auth?.access.isNullOrBlank()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.data.message?.takeIf { msg -> msg.isNotBlank() }
                                    ?: getString(Res.string.error_auth_missing_token)
                            )
                        }
                    } else {
                        authPreferences.saveTokens(
                            access = auth.access,
                            refresh = auth.refresh ?: "",
                            expiry = auth.tokenExpiry ?: 0L
                        )
                        authPreferences.saveLoggedInUser(result.data.user)
                        authPreferences.saveLoggedInUserInfo(result.data.userDetails)
                        authPreferences.saveLoginStatus(true)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isLoginSuccess = true
                            )
                        }
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
