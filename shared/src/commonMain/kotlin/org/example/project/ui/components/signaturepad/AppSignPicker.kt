package org.example.project.ui.components.signaturepad

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.imagepicker.ImagePickerUiState
import org.example.project.ui.components.imagepicker.ImagePickerViewModel
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.koin.compose.koinInject

@Composable
fun AppSignPicker(
    showPicker: MutableState<Boolean>,
    onIsUploading: ((Boolean) -> Unit)? = null,
    onSignatureUploaded: (String) -> Unit
) {
    val viewModel: ImagePickerViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    
    val showSignaturePad = remember { mutableStateOf(false) }

    val imagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let { bytes ->
                // Use image type 11 for signature upload as requested
                viewModel.uploadImage(bytes, 11)
            }
        }
    )

    SignatureSourcePickerBottomSheet(
        showSheet = showPicker.value,
        onDismissRequest = { showPicker.value = false },
        onDrawClick = {
            showPicker.value = false
            showSignaturePad.value = true
        },
        onUploadClick = {
            showPicker.value = false
            imagePicker.launch()
        }
    )
    
    if (showSignaturePad.value) {
        AppSignaturePadDialog(
            onDismissRequest = { showSignaturePad.value = false },
            onSignatureSaved = { bytes ->
                viewModel.uploadImage(bytes, 11)
            }
        )
    }

    // Removed AppLoader since we only need the inline loading overlay

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
            onSignatureUploaded((uiState as ImagePickerUiState.Success).imageUrl)
            viewModel.clearState()
        }
    }
}
