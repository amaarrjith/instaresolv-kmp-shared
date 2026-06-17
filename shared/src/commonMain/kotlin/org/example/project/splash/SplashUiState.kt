package org.example.project.splash

data class SplashUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loadingCompleted: Boolean = false
) {
}