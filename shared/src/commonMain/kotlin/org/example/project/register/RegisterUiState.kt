package org.example.project.register

data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccess: Boolean = false,
    val tempUserId: Int = 0,
    val email: String = ""
)