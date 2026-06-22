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
    imageType: Int = 1,
    showFullScreenLoader: Boolean = true,
    onIsUploading: ((Boolean) -> Unit)? = null,
    onImageUploaded: (String) -> Unit
) {
    val viewModel: ImagePickerViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()



    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let { bytes ->
                viewModel.uploadImage(bytes, imageType)
            }
        }
    )

    val cameraLauncher = rememberAppCameraLauncher(onResult = { bytes ->
        if (bytes != null) {
            viewModel.uploadImage(bytes, imageType)
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
            onImageUploaded((uiState as ImagePickerUiState.Success).imageUrl)
            viewModel.clearState()
        }
    }
}
