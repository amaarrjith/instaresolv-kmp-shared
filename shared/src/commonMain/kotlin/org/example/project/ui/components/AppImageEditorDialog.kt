package org.example.project.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
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
import org.example.project.utilites.DrawStrokeData
import org.example.project.utilites.NormalizedPoint
import org.example.project.utilites.drawAnnotationsOnImage
import org.example.project.utilites.toImageBitmap

data class UIStroke(
    val points: MutableList<NormalizedPoint>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun AppImageEditorDialog(
    imageBytes: ByteArray? = null,
    imageUrl: String? = null,
    onDismiss: () -> Unit,
    onEditApplied: (ByteArray) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val imageBitmap = remember(imageBytes) { imageBytes?.toImageBitmap() }

    var selectedColor by remember { mutableStateOf(Color(0xFFFF3B30)) } // Vibrant Red
    var strokeWidthPx by remember { mutableStateOf(8f) }
    val strokes = remember { mutableStateListOf<UIStroke>() }
    var currentStroke by remember { mutableStateOf<UIStroke?>(null) }
    var isApplying by remember { mutableStateOf(false) }

    val colorOptions = listOf(
        Color(0xFFFF3B30), // Red
        Color(0xFFFFCC00), // Yellow
        Color(0xFF34C759), // Green
        Color(0xFF007AFF), // Blue
        Color(0xFFFFFFFF), // White
        Color(0xFF000000)  // Black
    )

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
                        text = "Edit & Annotate",
                        style = textStyle(size = 18.sp, weight = FontWeight.Bold),
                        color = Color.White
                    )

                    TextButton(
                        onClick = {
                            if (!isApplying) {
                                isApplying = true
                                coroutineScope.launch {
                                    val finalBytes = if (imageBytes != null && strokes.isNotEmpty()) {
                                        val strokeDatas = strokes.map { s ->
                                            DrawStrokeData(
                                                points = s.points,
                                                colorInt = s.color.toArgb(),
                                                strokeWidth = s.strokeWidth
                                            )
                                        }
                                        drawAnnotationsOnImage(imageBytes, strokeDatas) ?: imageBytes
                                    } else {
                                        imageBytes ?: ByteArray(0)
                                    }
                                    onEditApplied(finalBytes)
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

                // Stage Area: Base Image + Annotation Canvas
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Base Image
                        if (imageBitmap != null) {
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = "Annotate Target",
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

                        // Annotation Canvas Over Base Image
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(selectedColor, strokeWidthPx) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            val w = size.width.toFloat()
                                            val h = size.height.toFloat()
                                            if (w > 0 && h > 0) {
                                                val point = NormalizedPoint(offset.x / w, offset.y / h)
                                                val stroke = UIStroke(
                                                    points = mutableListOf(point),
                                                    color = selectedColor,
                                                    strokeWidth = strokeWidthPx
                                                )
                                                currentStroke = stroke
                                                strokes.add(stroke)
                                            }
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            val w = size.width.toFloat()
                                            val h = size.height.toFloat()
                                            if (w > 0 && h > 0) {
                                                val pos = change.position
                                                val point = NormalizedPoint(pos.x / w, pos.y / h)
                                                currentStroke?.points?.add(point)
                                            }
                                        },
                                        onDragEnd = {
                                            currentStroke = null
                                        }
                                    )
                                }
                        ) {
                            val w = size.width
                            val h = size.height

                            strokes.forEach { strokeItem ->
                                if (strokeItem.points.isNotEmpty()) {
                                    val path = Path()
                                    val first = strokeItem.points.first()
                                    path.moveTo(first.x * w, first.y * h)
                                    for (pt in strokeItem.points.drop(1)) {
                                        path.lineTo(pt.x * w, pt.y * h)
                                    }
                                    drawPath(
                                        path = path,
                                        color = strokeItem.color,
                                        style = Stroke(
                                            width = strokeItem.strokeWidth,
                                            cap = StrokeCap.Round,
                                            join = StrokeJoin.Round
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Toolbar: Color Palette, Stroke Thickness & Undo
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E))
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Color Swatches
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        colorOptions.forEach { color ->
                            val isSelected = selectedColor == color
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = color }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stroke Size & Control Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Thin stroke
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (strokeWidthPx == 4f) AppColors.Primary else Color(0xFF2C2C2C))
                                .clickable { strokeWidthPx = 4f }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Thin",
                                style = textStyle(size = 13.sp, weight = FontWeight.Medium),
                                color = Color.White
                            )
                        }

                        // Medium stroke
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (strokeWidthPx == 8f) AppColors.Primary else Color(0xFF2C2C2C))
                                .clickable { strokeWidthPx = 8f }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Medium",
                                style = textStyle(size = 13.sp, weight = FontWeight.Medium),
                                color = Color.White
                            )
                        }

                        // Thick stroke
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (strokeWidthPx == 14f) AppColors.Primary else Color(0xFF2C2C2C))
                                .clickable { strokeWidthPx = 14f }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Thick",
                                style = textStyle(size = 13.sp, weight = FontWeight.Medium),
                                color = Color.White
                            )
                        }

                        // Undo Last Stroke
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2C2C2C))
                                .clickable {
                                    if (strokes.isNotEmpty()) {
                                        strokes.removeAt(strokes.lastIndex)
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "↩ Undo",
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
