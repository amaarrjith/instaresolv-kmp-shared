package org.example.project.forgetpassword

data class ForgetPasswordUiState(
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isPasswordResetSuccess: Boolean = false,
    val successMessage: String? = null
)