package org.example.project.ui.components

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.yalantis.ucrop.UCrop
import java.io.File

actual class NativeImageEditorLauncher(
    private val onLaunch: (ByteArray) -> Unit
) {
    actual fun launch(imageBytes: ByteArray) {
        onLaunch(imageBytes)
    }
}

@Composable
actual fun rememberNativeImageEditorLauncher(
    onResult: (ByteArray?) -> Unit
): NativeImageEditorLauncher {
    val context = LocalContext.current
    var editingBytes by remember { mutableStateOf<ByteArray?>(null) }

    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                try {
                    val file = File(resultUri.path ?: "")
                    if (file.exists()) {
                        onResult(file.readBytes())
                    } else {
                        val bytes = context.contentResolver.openInputStream(resultUri)?.use { it.readBytes() }
                        onResult(bytes)
                    }
                } catch (e: Exception) {
                    onResult(null)
                }
            } else {
                onResult(null)
            }
        } else {
            onResult(null)
        }
    }

    if (editingBytes != null) {
        Dialog(
            onDismissRequest = {
                editingBytes = null
                onResult(null)
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            EditorToolScreen(
                imageBytes = editingBytes!!,
                onClose = {
                    editingBytes = null
                    onResult(null)
                },
                onSave = { editedUri ->
                    editingBytes = null
                    val destinationUri = Uri.fromFile(
                        File(context.cacheDir, "cropped_image_${System.currentTimeMillis()}.jpg")
                    )
                    val options = UCrop.Options().apply {
                        setCompressionFormat(Bitmap.CompressFormat.JPEG)
                        setCompressionQuality(90)
                        setFreeStyleCropEnabled(true)
                    }
                    val intent = UCrop.of(editedUri, destinationUri)
                        .withOptions(options)
                        .getIntent(context)
                    cropLauncher.launch(intent)
                }
            )
        }
    }

    return remember {
        NativeImageEditorLauncher(
            onLaunch = { bytes ->
                editingBytes = bytes
            }
        )
    }
}
