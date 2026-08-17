package org.example.project.utilites

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
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

actual fun ByteArray.toImageBitmap(): ImageBitmap? {
    return try {
        val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
        bitmap?.asImageBitmap()
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
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) ?: return imageBytes
        val x = (leftRatio * bitmap.width).toInt().coerceIn(0, bitmap.width - 1)
        val y = (topRatio * bitmap.height).toInt().coerceIn(0, bitmap.height - 1)
        val w = ((rightRatio - leftRatio) * bitmap.width).toInt().coerceIn(1, bitmap.width - x)
        val h = ((bottomRatio - topRatio) * bitmap.height).toInt().coerceIn(1, bitmap.height - y)
        
        val croppedBitmap = Bitmap.createBitmap(bitmap, x, y, w, h)
        val stream = ByteArrayOutputStream()
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        stream.toByteArray()
    } catch (e: Exception) {
        imageBytes
    }
}

actual fun drawAnnotationsOnImage(
    imageBytes: ByteArray,
    strokes: List<DrawStrokeData>
): ByteArray? {
    return try {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) ?: return imageBytes
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        
        val scale = (mutableBitmap.width / 400f).coerceAtLeast(1f)

        for (stroke in strokes) {
            paint.color = stroke.colorInt
            paint.strokeWidth = stroke.strokeWidth * scale
            val path = Path()
            if (stroke.points.isNotEmpty()) {
                val first = stroke.points.first()
                path.moveTo(first.x * mutableBitmap.width, first.y * mutableBitmap.height)
                for (pt in stroke.points.drop(1)) {
                    path.lineTo(pt.x * mutableBitmap.width, pt.y * mutableBitmap.height)
                }
            }
            canvas.drawPath(path, paint)
        }
        
        val stream = ByteArrayOutputStream()
        mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        stream.toByteArray()
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
    val context = LocalContext.current
    val bitmap = remember(imageBytes) { BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) }

    if (bitmap != null) {
        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                com.canhub.cropper.CropImageView(ctx).apply {
                    guidelines = com.canhub.cropper.CropImageView.Guidelines.ON
                    setImageBitmap(bitmap)
                    setOnCropImageCompleteListener { _, result ->
                        if (result.isSuccessful) {
                            result.bitmap?.let { cropped ->
                                val stream = ByteArrayOutputStream()
                                cropped.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                                onCropped(stream.toByteArray())
                            }
                        } else {
                            onCancel()
                        }
                    }
                }
            },
            update = { cropView ->
                cropView.setImageBitmap(bitmap)
            }
        )
    }
}
