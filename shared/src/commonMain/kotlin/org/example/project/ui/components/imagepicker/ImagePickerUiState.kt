package org.example.project.ui.components.imagepicker

sealed class ImagePickerUiState {
    object Ready : ImagePickerUiState()
    object Uploading : ImagePickerUiState()
    data class Success(val imageUrl: String, val isAIDescriptionLoading: Boolean = false, val aiDescription: String? = null, val aiDescriptionFailed: Boolean = false) : ImagePickerUiState()
    data class Error(val errorMessage: String) : ImagePickerUiState()
}
