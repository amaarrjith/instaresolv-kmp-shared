package org.example.project.utilites

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual fun ImageBitmap.toByteArray(): ByteArray? {
    val skiaBitmap = this.asSkiaBitmap()
    val image = Image.makeFromBitmap(skiaBitmap)
    return image.encodeToData(EncodedImageFormat.PNG)?.bytes
}
