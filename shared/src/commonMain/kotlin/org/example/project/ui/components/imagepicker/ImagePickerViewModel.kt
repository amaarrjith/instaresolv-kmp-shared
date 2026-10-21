package org.example.project.ui.components.imagepicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult
import org.example.project.utilites.compressImage
import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.getString

class ImagePickerViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _uiState = MutableStateFlow<ImagePickerUiState>(ImagePickerUiState.Ready)
    val uiState = _uiState.asStateFlow()

    private var lastUploadedImageBytes: ByteArray? = null

    fun uploadImage(imageBytes: ByteArray, type: Int) {
        val compressedBytes = compressImage(imageBytes)
        lastUploadedImageBytes = compressedBytes
        viewModelScope.launch {
            _uiState.value = ImagePickerUiState.Uploading
            val response = authRepository.uploadImage(
                imageBytes = compressedBytes,
                fileName = "picked_image.jpg",
                type = type
            )
            when (response) {
                is NetworkResult.Success -> {
                    response.data.imageUrl?.let { url ->
                        _uiState.value = ImagePickerUiState.Success(url)
                    } ?: run {
                        _uiState.value = ImagePickerUiState.Error(getString(Res.string.error_failed_to_get_image_url))
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.value = ImagePickerUiState.Error(response.message ?: getString(Res.string.unknown_error))
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
