package org.example.project.utilites

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File

actual class AppCameraLauncher(
    private val onLaunch: () -> Unit
) {
    actual fun launch() {
        onLaunch()
    }
}

@Composable
actual fun rememberAppCameraLauncher(onResult: (ByteArray?) -> Unit): AppCameraLauncher {
    val context = LocalContext.current
    val photoFile = remember {
        val dir = File(context.cacheDir, "camera_photos").apply { mkdirs() }
        File(dir, "temp_capture.jpg")
    }
    val photoUri: Uri = remember(photoFile) {
        val authority = "${context.packageName}.fileprovider"
        FileProvider.getUriForFile(context, authority, photoFile)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoFile.exists()) {
            try {
                // Downsample large camera photos to max 2048px to prevent OutOfMemoryError while maintaining crisp quality
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(photoFile.absolutePath, options)
                val maxDim = maxOf(options.outWidth, options.outHeight)
                var sampleSize = 1
                while (maxDim / sampleSize > 2048) {
                    sampleSize *= 2
                }
                val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath, decodeOptions)
                if (bitmap != null) {
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
                    onResult(stream.toByteArray())
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                onResult(null)
            } finally {
                photoFile.delete()
            }
        } else {
            onResult(null)
        }
    }

    return remember {
        AppCameraLauncher(
            onLaunch = { launcher.launch(photoUri) }
        )
    }
}
