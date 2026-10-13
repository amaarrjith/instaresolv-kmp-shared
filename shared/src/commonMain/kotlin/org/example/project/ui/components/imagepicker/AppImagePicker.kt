package org.example.project.ui.components.imagepicker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import org.example.project.utilites.rememberAppCameraLauncher
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.ImageSourcePickerBottomSheet
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.koin.compose.koinInject

@Composable
fun AppImagePicker(
    showPicker: MutableState<Boolean>,
    isAIDescriptionEnabled: Boolean? = false,
    imageType: Int = 1,
    showFullScreenLoader: Boolean = true,
    pendingUploadBytes: ByteArray? = null,
    onIsUploading: ((Boolean) -> Unit)? = null,
    onAiDescriptionLoading: ((Boolean) -> Unit)? = null,
    onAiDescriptionSuccess: ((String) -> Unit)? = null,
    onImagePicked: ((ByteArray) -> Unit)? = null,
    onImageUploaded: (String) -> Unit
) {
    val viewModel: ImagePickerViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(pendingUploadBytes) {
        if (pendingUploadBytes != null) {
            viewModel.uploadImage(pendingUploadBytes, imageType)
        }
    }

    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let { bytes ->
                if (onImagePicked != null) {
                    onImagePicked(bytes)
                } else {
                    viewModel.uploadImage(bytes, imageType)
                }
            }
        }
    )

    val cameraLauncher = rememberAppCameraLauncher(onResult = { bytes ->
        if (bytes != null) {
            if (onImagePicked != null) {
                onImagePicked(bytes)
            } else {
                viewModel.uploadImage(bytes, imageType)
            }
        }
    })

    ImageSourcePickerBottomSheet(
        showSheet = showPicker.value,
        onDismissRequest = { showPicker.value = false },
        onCameraClick = {
            showPicker.value = false
            cameraLauncher.launch()
        },
        onGalleryClick = {
            showPicker.value = false
            imagePicker.launch()
        }
    )

    if (uiState is ImagePickerUiState.Uploading && showFullScreenLoader) {
        AppLoader()
    }

    Box {
        ToastHost(
            visible = uiState is ImagePickerUiState.Error,
            message = (uiState as? ImagePickerUiState.Error)?.errorMessage.orEmpty(),
            onDismiss = {
                viewModel.clearState()
            },
            type = ToastType.Error
        )
    }

    LaunchedEffect(uiState) {
        onIsUploading?.invoke(uiState is ImagePickerUiState.Uploading)
        if (uiState is ImagePickerUiState.Success) {
            val successState = uiState as ImagePickerUiState.Success
            if (successState.isAIDescriptionLoading) {
                onAiDescriptionLoading?.invoke(true)
            } else if (successState.aiDescription != null || successState.aiDescriptionFailed) {
                onAiDescriptionLoading?.invoke(false)
                onImageUploaded(successState.imageUrl)
                successState.aiDescription?.let {
                    onAiDescriptionSuccess?.invoke(it)
                }
            } else if (isAIDescriptionEnabled == true) {
                onAiDescriptionLoading?.invoke(true)
                viewModel.generateAiDescription(successState.imageUrl)
            } else {
                onImageUploaded(successState.imageUrl)
            }
        }
    }
}
