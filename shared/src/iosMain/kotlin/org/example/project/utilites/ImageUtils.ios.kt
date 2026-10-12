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

private const val MAX_IMAGE_DIMENSION = 2048

actual fun ImageBitmap.toByteArray(): ByteArray? {
    val skiaBitmap = this.asSkiaBitmap()
    val image = Image.makeFromBitmap(skiaBitmap)
    val bytes = image.encodeToData(EncodedImageFormat.PNG)?.bytes
    image.close()
    return bytes
}

actual fun ByteArray.toImageBitmap(): ImageBitmap? {
    return try {
        val skiaImage = Image.makeFromEncoded(this)
        if (skiaImage.width > MAX_IMAGE_DIMENSION || skiaImage.height > MAX_IMAGE_DIMENSION) {
            val scale = minOf(
                MAX_IMAGE_DIMENSION.toFloat() / skiaImage.width,
                MAX_IMAGE_DIMENSION.toFloat() / skiaImage.height
            )
            val targetW = (skiaImage.width * scale).toInt().coerceAtLeast(1)
            val targetH = (skiaImage.height * scale).toInt().coerceAtLeast(1)
            val surface = Surface.makeRasterN32Premul(targetW, targetH)
            surface.canvas.drawImageRect(
                skiaImage,
                Rect.makeWH(skiaImage.width.toFloat(), skiaImage.height.toFloat()),
                Rect.makeWH(targetW.toFloat(), targetH.toFloat())
            )
            val snapshot = surface.makeImageSnapshot()
            val bitmap = Bitmap.makeFromImage(snapshot)
            snapshot.close()
            surface.close()
            skiaImage.close()
            bitmap.asComposeImageBitmap()
        } else {
            val bitmap = Bitmap.makeFromImage(skiaImage)
            skiaImage.close()
            bitmap.asComposeImageBitmap()
        }
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
        
        val scale = if (w > MAX_IMAGE_DIMENSION || h > MAX_IMAGE_DIMENSION) {
            minOf(
                MAX_IMAGE_DIMENSION.toFloat() / w,
                MAX_IMAGE_DIMENSION.toFloat() / h
            )
        } else {
            1f
        }
        val dstW = (w * scale).toInt().coerceAtLeast(1)
        val dstH = (h * scale).toInt().coerceAtLeast(1)

        val surface = Surface.makeRasterN32Premul(dstW, dstH)
        val srcRect = Rect.makeLTRB(x.toFloat(), y.toFloat(), (x + w).toFloat(), (y + h).toFloat())
        val dstRect = Rect.makeWH(dstW.toFloat(), dstH.toFloat())
        
        surface.canvas.drawImageRect(skiaImage, srcRect, dstRect)
        val snapshot = surface.makeImageSnapshot()
        val result = snapshot.encodeToData(EncodedImageFormat.JPEG, 90)?.bytes ?: imageBytes
        snapshot.close()
        surface.close()
        skiaImage.close()
        result
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
        val scale = if (skiaImage.width > MAX_IMAGE_DIMENSION || skiaImage.height > MAX_IMAGE_DIMENSION) {
            minOf(
                MAX_IMAGE_DIMENSION.toFloat() / skiaImage.width,
                MAX_IMAGE_DIMENSION.toFloat() / skiaImage.height
            )
        } else {
            1f
        }
        val targetW = (skiaImage.width * scale).toInt().coerceAtLeast(1)
        val targetH = (skiaImage.height * scale).toInt().coerceAtLeast(1)

        val surface = Surface.makeRasterN32Premul(targetW, targetH)
        surface.canvas.drawImageRect(
            skiaImage,
            Rect.makeWH(skiaImage.width.toFloat(), skiaImage.height.toFloat()),
            Rect.makeWH(targetW.toFloat(), targetH.toFloat())
        )
        
        val strokeScale = (targetW / 400f).coerceAtLeast(1f)

        for (stroke in strokes) {
            val paint = Paint().apply {
                color = stroke.colorInt
                strokeWidth = stroke.strokeWidth * strokeScale
                mode = PaintMode.STROKE
                strokeCap = PaintStrokeCap.ROUND
                strokeJoin = PaintStrokeJoin.ROUND
            }
            if (stroke.points.isNotEmpty()) {
                val builder = PathBuilder()
                val first = stroke.points.first()
                builder.moveTo(first.x * targetW, first.y * targetH)
                for (pt in stroke.points.drop(1)) {
                    builder.lineTo(pt.x * targetW, pt.y * targetH)
                }
                val skiaPath = builder.snapshot()
                surface.canvas.drawPath(skiaPath, paint)
                skiaPath.close()
            }
            paint.close()
        }
        
        val snapshot = surface.makeImageSnapshot()
        val result = snapshot.encodeToData(EncodedImageFormat.JPEG, 90)?.bytes ?: imageBytes
        snapshot.close()
        surface.close()
        skiaImage.close()
        result
    } catch (e: Exception) {
        imageBytes
    }
}
