package org.example.project.utilites

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.toByteArray(): ByteArray? {
    val bitmap = this.asAndroidBitmap()
    val stream = ByteArrayOutputStream()
    return if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
        stream.toByteArray()
    } else {
        null
    }
}
