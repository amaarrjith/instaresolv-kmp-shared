package org.example.project.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.utilites.cropImage
import org.example.project.utilites.toImageBitmap

enum class CropAspectRatio(val label: String, val ratioValue: Float?) {
    FREE("Free", null),
    SQUARE("1:1", 1.0f),
    RATIO_4_3("4:3", 4.0f / 3.0f),
    RATIO_16_9("16:9", 16.0f / 9.0f)
}

@Composable
fun AppImageCropDialog(
    imageBytes: ByteArray? = null,
    imageUrl: String? = null,
    onDismiss: () -> Unit,
    onCropApplied: (ByteArray) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val imageBitmap = remember(imageBytes) { imageBytes?.toImageBitmap() }

    var rotationAngle by remember { mutableStateOf(0f) }
    var selectedRatio by remember { mutableStateOf(CropAspectRatio.FREE) }
    var isApplying by remember { mutableStateOf(false) }

    // Normalized Crop Window coordinates (0.0 .. 1.0)
    var cropLeft by remember { mutableStateOf(0.05f) }
    var cropTop by remember { mutableStateOf(0.05f) }
    var cropRight by remember { mutableStateOf(0.95f) }
    var cropBottom by remember { mutableStateOf(0.95f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Cancel",
                            style = textStyle(size = 16.sp, weight = FontWeight.Medium),
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Crop Image",
                        style = textStyle(size = 18.sp, weight = FontWeight.Bold),
                        color = Color.White
                    )

                    TextButton(
                        onClick = {
                            if (!isApplying) {
                                isApplying = true
                                coroutineScope.launch {
                                    val cropped = if (imageBytes != null) {
                                        cropImage(imageBytes, cropLeft, cropTop, cropRight, cropBottom) ?: imageBytes
                                    } else ByteArray(0)
                                    onCropApplied(cropped)
                                }
                            }
                        },
                        enabled = !isApplying
                    ) {
                        if (isApplying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = AppColors.Primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Done",
                                style = textStyle(size = 16.sp, weight = FontWeight.Bold),
                                color = AppColors.Primary
                            )
                        }
                    }
                }

                // Interactive Crop Stage
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Image Container
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationZ = rotationAngle
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageBitmap != null) {
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = "Crop Target",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else if (imageUrl != null) {
                            WebImageView(
                                imageUrl = imageUrl,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    // Interactive Grid & Mask Overlay
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(selectedRatio) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val width = size.width.toFloat()
                                    val height = size.height.toFloat()

                                    if (width > 0 && height > 0) {
                                        val deltaX = dragAmount.x / width
                                        val deltaY = dragAmount.y / height

                                        val maxLeft = (cropRight - 0.1f).coerceAtLeast(0f)
                                        val minRight = (cropLeft + 0.1f).coerceAtMost(1f)
                                        val maxTop = (cropBottom - 0.1f).coerceAtLeast(0f)
                                        val minBottom = (cropTop + 0.1f).coerceAtMost(1f)

                                        var newLeft = (cropLeft + deltaX).coerceIn(0f, maxLeft)
                                        var newRight = (cropRight + deltaX).coerceIn(minRight, 1f)
                                        var newTop = (cropTop + deltaY).coerceIn(0f, maxTop)
                                        var newBottom = (cropBottom + deltaY).coerceIn(minBottom, 1f)

                                        selectedRatio.ratioValue?.let { r ->
                                            val currentWidthPx = (newRight - newLeft) * width
                                            val targetHeightPx = currentWidthPx / r
                                            val targetHeightFrac = targetHeightPx / height
                                            newBottom = (newTop + targetHeightFrac).coerceAtMost(1f)
                                        }

                                        cropLeft = newLeft
                                        cropRight = newRight
                                        cropTop = newTop
                                        cropBottom = newBottom
                                    }
                                }
                            }
                    ) {
                        val w = size.width
                        val h = size.height

                        val rectLeft = cropLeft * w
                        val rectTop = cropTop * h
                        val rectRight = cropRight * w
                        val rectBottom = cropBottom * h
                        val rectW = (rectRight - rectLeft).coerceAtLeast(0f)
                        val rectH = (rectBottom - rectTop).coerceAtLeast(0f)

                        val darkColor = Color.Black.copy(alpha = 0.65f)

                        // 4 Darkened overlays around the crop rect (NO BlendMode.Clear to prevent hole-punching)
                        drawRect(
                            color = darkColor,
                            topLeft = Offset(0f, 0f),
                            size = Size(w, rectTop)
                        )
                        drawRect(
                            color = darkColor,
                            topLeft = Offset(0f, rectBottom),
                            size = Size(w, (h - rectBottom).coerceAtLeast(0f))
                        )
                        drawRect(
                            color = darkColor,
                            topLeft = Offset(0f, rectTop),
                            size = Size(rectLeft, rectH)
                        )
                        drawRect(
                            color = darkColor,
                            topLeft = Offset(rectRight, rectTop),
                            size = Size((w - rectRight).coerceAtLeast(0f), rectH)
                        )

                        // Crop Box Border
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(rectLeft, rectTop),
                            size = Size(rectW, rectH),
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Rule of Thirds Grid Lines inside Crop Box
                        val colStep = rectW / 3f
                        val rowStep = rectH / 3f

                        for (i in 1..2) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.4f),
                                start = Offset(rectLeft + colStep * i, rectTop),
                                end = Offset(rectLeft + colStep * i, rectBottom),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = Color.White.copy(alpha = 0.4f),
                                start = Offset(rectLeft, rectTop + rowStep * i),
                                end = Offset(rectRight, rectTop + rowStep * i),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Corner Handles
                        val handleLen = 20.dp.toPx()
                        val handleStroke = 4.dp.toPx()

                        // Top-Left Corner
                        drawLine(Color.White, Offset(rectLeft - 2, rectTop), Offset(rectLeft + handleLen, rectTop), handleStroke)
                        drawLine(Color.White, Offset(rectLeft, rectTop - 2), Offset(rectLeft, rectTop + handleLen), handleStroke)

                        // Top-Right Corner
                        drawLine(Color.White, Offset(rectRight + 2, rectTop), Offset(rectRight - handleLen, rectTop), handleStroke)
                        drawLine(Color.White, Offset(rectRight, rectTop - 2), Offset(rectRight, rectTop + handleLen), handleStroke)

                        // Bottom-Left Corner
                        drawLine(Color.White, Offset(rectLeft - 2, rectBottom), Offset(rectLeft + handleLen, rectBottom), handleStroke)
                        drawLine(Color.White, Offset(rectLeft, rectBottom + 2), Offset(rectLeft, rectBottom - handleLen), handleStroke)

                        // Bottom-Right Corner
                        drawLine(Color.White, Offset(rectRight + 2, rectBottom), Offset(rectRight - handleLen, rectBottom), handleStroke)
                        drawLine(Color.White, Offset(rectRight, rectBottom + 2), Offset(rectRight, rectBottom - handleLen), handleStroke)
                    }
                }

                // Aspect Ratio Selector & Tools Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E))
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CropAspectRatio.entries.forEach { ratioOption ->
                            val isSelected = selectedRatio == ratioOption
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) AppColors.Primary else Color.Transparent)
                                    .clickable {
                                        selectedRatio = ratioOption
                                        if (ratioOption.ratioValue != null) {
                                            cropLeft = 0.15f
                                            cropTop = 0.15f
                                            cropRight = 0.85f
                                            cropBottom = 0.85f
                                        }
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = ratioOption.label,
                                    style = textStyle(size = 13.sp, weight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                                    color = if (isSelected) Color.White else Color.LightGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Rotate 90 deg
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2C2C2C))
                                .clickable { rotationAngle = (rotationAngle + 90f) % 360f }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "↺ Rotate",
                                style = textStyle(size = 13.sp, weight = FontWeight.Medium),
                                color = Color.White
                            )
                        }

                        // Reset Crop Box
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2C2C2C))
                                .clickable {
                                    cropLeft = 0.05f
                                    cropTop = 0.05f
                                    cropRight = 0.95f
                                    cropBottom = 0.95f
                                    rotationAngle = 0f
                                    selectedRatio = CropAspectRatio.FREE
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Reset",
                                style = textStyle(size = 13.sp, weight = FontWeight.Medium),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
