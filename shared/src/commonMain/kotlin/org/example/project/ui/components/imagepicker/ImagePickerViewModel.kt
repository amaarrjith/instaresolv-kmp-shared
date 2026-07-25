package org.example.project.ui.components.imagepicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult

class ImagePickerViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<ImagePickerUiState>(ImagePickerUiState.Ready)
    val uiState = _uiState.asStateFlow()

    private var lastUploadedImageBytes: ByteArray? = null

    fun uploadImage(imageBytes: ByteArray, type: Int) {
        lastUploadedImageBytes = imageBytes
        viewModelScope.launch {
            _uiState.value = ImagePickerUiState.Uploading
            val response = authRepository.uploadImage(
                imageBytes = imageBytes,
                fileName = "picked_image.jpg",
                type = type
            )
            when (response) {
                is NetworkResult.Success -> {
                    response.data.imageUrl?.let { url ->
                        _uiState.value = ImagePickerUiState.Success(url)
                    } ?: run {
                        _uiState.value = ImagePickerUiState.Error("Failed to get image URL")
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = ImagePickerUiState.Error(response.message ?: "Unknown Error")
                }
            }
        }
    }

    fun clearState() {
        _uiState.value = ImagePickerUiState.Ready
        lastUploadedImageBytes = null
    }

    fun generateAiDescription(imageUrl: String) {
        val bytes = lastUploadedImageBytes
        if (bytes == null) {
            _uiState.value = ImagePickerUiState.Success(imageUrl, false, null, true)
            return
        }
        
        viewModelScope.launch {
            _uiState.value = ImagePickerUiState.Success(imageUrl, true, null)
            val response = authRepository.analyzeImage(bytes, "picked_image.jpg")
            when (response) {
                is NetworkResult.Success -> {
                    _uiState.value = ImagePickerUiState.Success(imageUrl, false, response.data.imageDescription)
                }
                is NetworkResult.Error -> {
                    _uiState.value = ImagePickerUiState.Success(imageUrl, false, null, true)
                }
            }
        }
    }
}
