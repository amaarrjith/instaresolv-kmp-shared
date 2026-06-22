package org.example.project.utilites

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.ByteArrayOutputStream

actual class AppCameraLauncher(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        onLaunch()
    }
}

@Composable
actual fun rememberAppCameraLauncher(onResult: (ByteArray?) -> Unit): AppCameraLauncher {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            onResult(stream.toByteArray())
        } else {
            onResult(null)
        }
    }
    return remember {
        AppCameraLauncher(
            onLaunch = { launcher.launch(null) }
        )
    }
}
