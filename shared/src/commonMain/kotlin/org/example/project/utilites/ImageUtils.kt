package org.example.project.utilites

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap

data class NormalizedPoint(val x: Float, val y: Float)

data class DrawStrokeData(
    val points: List<NormalizedPoint>,
    val colorInt: Int,
    val strokeWidth: Float
)

expect fun ImageBitmap.toByteArray(): ByteArray?
expect fun ByteArray.toImageBitmap(): ImageBitmap?

expect fun cropImage(
    imageBytes: ByteArray,
    leftRatio: Float,
    topRatio: Float,
    rightRatio: Float,
    bottomRatio: Float
): ByteArray?

expect fun drawAnnotationsOnImage(
    imageBytes: ByteArray,
    strokes: List<DrawStrokeData>
): ByteArray?

@Composable
expect fun AppPlatformCropView(
    imageBytes: ByteArray,
    onCropped: (ByteArray) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier
)
