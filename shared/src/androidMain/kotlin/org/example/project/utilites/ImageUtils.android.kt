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

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

private fun decodeSampledBitmapFromByteArray(data: ByteArray, reqWidth: Int = 2048, reqHeight: Int = 2048): Bitmap? {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeByteArray(data, 0, data.size, options)
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeByteArray(data, 0, data.size, options)
}

actual fun ByteArray.toImageBitmap(): ImageBitmap? {
    return try {
        val bitmap = decodeSampledBitmapFromByteArray(this, 2048, 2048)
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
        val bitmap = decodeSampledBitmapFromByteArray(imageBytes, 2048, 2048) ?: return imageBytes
        val x = (leftRatio * bitmap.width).toInt().coerceIn(0, bitmap.width - 1)
        val y = (topRatio * bitmap.height).toInt().coerceIn(0, bitmap.height - 1)
        val w = ((rightRatio - leftRatio) * bitmap.width).toInt().coerceIn(1, bitmap.width - x)
        val h = ((bottomRatio - topRatio) * bitmap.height).toInt().coerceIn(1, bitmap.height - y)
        
        val croppedBitmap = Bitmap.createBitmap(bitmap, x, y, w, h)
        if (croppedBitmap != bitmap) {
            bitmap.recycle()
        }
        val stream = ByteArrayOutputStream()
        croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        croppedBitmap.recycle()
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
        val bitmap = decodeSampledBitmapFromByteArray(imageBytes, 2048, 2048) ?: return imageBytes
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        bitmap.recycle()
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
        mutableBitmap.recycle()
        stream.toByteArray()
    } catch (e: Exception) {
        imageBytes
    }
}
