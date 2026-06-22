package org.example.project.profile

sealed class ProfileUiState {
    data object Loading: ProfileUiState()
    data class Success(val successMessage: String): ProfileUiState()
    data class Error(val message: String): ProfileUiState()
    data object isEditing: ProfileUiState()
    data object Ready: ProfileUiState()
}