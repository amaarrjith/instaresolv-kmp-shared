package org.example.project.ui.components.imagepicker

sealed class ImagePickerUiState {
    object Ready : ImagePickerUiState()
    object Uploading : ImagePickerUiState()
    data class Success(val imageUrl: String) : ImagePickerUiState()
    data class Error(val errorMessage: String) : ImagePickerUiState()
}
