package org.example.project.utilites

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
    val currentOnResult by rememberUpdatedState(onResult)
    var currentPhotoFile by remember { mutableStateOf<File?>(null) }
    var currentPhotoUri by remember { mutableStateOf<Uri?>(null) }

    fun prepareFileAndUri(): Pair<File, Uri>? {
        return try {
            val dir = File(context.cacheDir, "camera_photos").apply { mkdirs() }
            val file = File(dir, "temp_capture_${System.currentTimeMillis()}.jpg")
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)
            Pair(file, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        val file = currentPhotoFile
        if (success && file != null && file.exists() && file.length() > 0) {
            try {
                // Read EXIF orientation before deleting or altering the captured photo file
                var orientation = getExifOrientation(file.absolutePath)
                if (orientation == ExifInterface.ORIENTATION_UNDEFINED ||
                    orientation == ExifInterface.ORIENTATION_NORMAL
                ) {
                    val uri = currentPhotoUri
                    if (uri != null) {
                        try {
                            context.contentResolver.openInputStream(uri)?.use { stream ->
                                orientation = ExifInterface(stream).getAttributeInt(
                                    ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_NORMAL
                                )
                            }
                        } catch (e: Exception) {
                            // ignore fallback error
                        }
                    }
                }

                // Downsample large camera photos to max 2048px to prevent OutOfMemoryError while maintaining crisp quality
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(file.absolutePath, options)
                val maxDim = maxOf(options.outWidth, options.outHeight)
                var sampleSize = 1
                while (maxDim / sampleSize > 2048) {
                    sampleSize *= 2
                }
                val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
                val bitmap = BitmapFactory.decodeFile(file.absolutePath, decodeOptions)
                if (bitmap != null) {
                    val orientedBitmap = rotateBitmapByOrientation(bitmap, orientation)
                    val stream = ByteArrayOutputStream()
                    orientedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
                    orientedBitmap.recycle()
                    currentOnResult(stream.toByteArray())
                } else {
                    currentOnResult(null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                currentOnResult(null)
            } finally {
                file.delete()
                currentPhotoFile = null
                currentPhotoUri = null
            }
        } else {
            file?.delete()
            currentPhotoFile = null
            currentPhotoUri = null
            currentOnResult(null)
        }
    }

    fun launchCameraInternal() {
        val pair = prepareFileAndUri()
        if (pair != null) {
            currentPhotoFile = pair.first
            currentPhotoUri = pair.second
            try {
                takePictureLauncher.launch(pair.second)
            } catch (e: Exception) {
                e.printStackTrace()
                pair.first.delete()
                currentPhotoFile = null
                currentPhotoUri = null
                currentOnResult(null)
            }
        } else {
            currentOnResult(null)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCameraInternal()
        } else {
            currentOnResult(null)
        }
    }

    val launchAction = rememberUpdatedState {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            launchCameraInternal()
        } else {
            try {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            } catch (e: Exception) {
                e.printStackTrace()
                currentOnResult(null)
            }
        }
    }

    return remember {
        AppCameraLauncher(
            onLaunch = { launchAction.value() }
        )
    }
}

