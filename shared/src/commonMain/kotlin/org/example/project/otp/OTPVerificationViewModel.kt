package org.example.project.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.NetworkResult
import org.example.project.domain.validation.OTPValidator
import org.example.project.domain.validation.RegisterValidator

class OTPVerificationViewModel(
    private val repository: AuthRepository,
    private val validator: OTPValidator,
    private val email: String,
    private val tempUserId: Int
): ViewModel() {
    private val _uiState = MutableStateFlow(OTPUiState())
    val uiState = _uiState.asStateFlow()

    fun verifyOTP(otp: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                )
            }
            validator.validateOTP(otp)?.let { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = error
                    )
                }
                return@launch
            }
            val result = repository.verifyOTP(
                tempUserId,
                email = email,
                otp = otp
            )
            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isOTPVerified = result.data.otpVerified,
                            errorMessage = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { state ->
                        state.copy(
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