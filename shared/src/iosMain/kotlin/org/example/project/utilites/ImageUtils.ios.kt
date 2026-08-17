package org.example.project.utilites

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.PaintStrokeCap
import org.jetbrains.skia.PaintStrokeJoin
import org.jetbrains.skia.PathBuilder
import org.jetbrains.skia.Rect
import org.jetbrains.skia.Surface

actual fun ImageBitmap.toByteArray(): ByteArray? {
    val skiaBitmap = this.asSkiaBitmap()
    val image = Image.makeFromBitmap(skiaBitmap)
    return image.encodeToData(EncodedImageFormat.PNG)?.bytes
}

actual fun ByteArray.toImageBitmap(): ImageBitmap? {
    return try {
        val skiaImage = Image.makeFromEncoded(this)
        Bitmap.makeFromImage(skiaImage).asComposeImageBitmap()
    } catch (e: Exception) {
        null
    }
}

actual fun cropImage(
    imageBytes: ByteArray,
    leftRatio: Float,
    topRatio: Float,
    rightRatio: Float,
    bottomRatio: Float
): ByteArray? {
    return try {
        val skiaImage = Image.makeFromEncoded(imageBytes)
        val imgWidth = skiaImage.width
        val imgHeight = skiaImage.height
        
        val x = (leftRatio * imgWidth).toInt().coerceIn(0, imgWidth - 1)
        val y = (topRatio * imgHeight).toInt().coerceIn(0, imgHeight - 1)
        val w = ((rightRatio - leftRatio) * imgWidth).toInt().coerceIn(1, imgWidth - x)
        val h = ((bottomRatio - topRatio) * imgHeight).toInt().coerceIn(1, imgHeight - y)
        
        val surface = Surface.makeRasterN32Premul(w, h)
        val srcRect = Rect.makeLTRB(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat())
        val dstRect = Rect.makeWH(w.toFloat(), h.toFloat())
        
        surface.canvas.drawImageRect(skiaImage, srcRect, dstRect)
        val snapshot = surface.makeImageSnapshot()
        snapshot.encodeToData(EncodedImageFormat.JPEG, 90)?.bytes ?: imageBytes
    } catch (e: Exception) {
        imageBytes
    }
}

actual fun drawAnnotationsOnImage(
    imageBytes: ByteArray,
    strokes: List<DrawStrokeData>
): ByteArray? {
    return try {
        val skiaImage = Image.makeFromEncoded(imageBytes)
        val w = skiaImage.width
        val h = skiaImage.height
        val surface = Surface.makeRasterN32Premul(w, h)
        
        surface.canvas.drawImage(skiaImage, 0f, 0f)
        
        val scale = (w / 400f).coerceAtLeast(1f)

        for (stroke in strokes) {
            val paint = Paint().apply {
                color = stroke.colorInt
                strokeWidth = stroke.strokeWidth * scale
                mode = PaintMode.STROKE
                strokeCap = PaintStrokeCap.ROUND
                strokeJoin = PaintStrokeJoin.ROUND
            }
            if (stroke.points.isNotEmpty()) {
                val builder = PathBuilder()
                val first = stroke.points.first()
                builder.moveTo(first.x * w, first.y * h)
                for (pt in stroke.points.drop(1)) {
                    builder.lineTo(pt.x * w, pt.y * h)
                }
                val skiaPath = builder.snapshot()
                surface.canvas.drawPath(skiaPath, paint)
            }
        }
        
        val snapshot = surface.makeImageSnapshot()
        snapshot.encodeToData(EncodedImageFormat.JPEG, 90)?.bytes ?: imageBytes
    } catch (e: Exception) {
        imageBytes
    }
}

@Composable
actual fun AppPlatformCropView(
    imageBytes: ByteArray,
    onCropped: (ByteArray) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier
) {
    val imageBitmap = remember(imageBytes) { imageBytes.toImageBitmap() }
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Crop Target",
            modifier = modifier,
            contentScale = ContentScale.Fit
        )
    }
}
