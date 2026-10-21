package org.example.project.otp

data class OTPUiState(
    val isLoading: Boolean = false,
    val isResending: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isOTPVerified: Boolean = false
)
