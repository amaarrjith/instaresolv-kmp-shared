package org.example.project.ui.components.signaturepad

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.utilites.toByteArray

@Composable
fun AppSignaturePadDialog(
    onDismissRequest: () -> Unit,
    onSignatureSaved: (ByteArray) -> Unit
) {
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var recomposeTrigger by remember { mutableStateOf(0) }

    val graphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = Color.White,
        title = {
            Text(
                text = "Draw Signature",
                style = textStyle(size = 18.sp, weight = FontWeight.Bold),
                color = AppColors.Primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPath = Path().apply { moveTo(offset.x, offset.y) }
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPath?.lineTo(change.position.x, change.position.y)
                                    recomposeTrigger++
                                },
                                onDragEnd = {
                                    currentPath?.let { paths.add(it) }
                                    currentPath = null
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val trigger = recomposeTrigger
                        graphicsLayer.record {
                            drawRect(Color.White)
                            paths.forEach { path ->
                                drawPath(
                                    path = path,
                                    color = Color.Black,
                                    style = Stroke(
                                        width = 5f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                            currentPath?.let { path ->
                                drawPath(
                                    path = path,
                                    color = Color.Black,
                                    style = Stroke(
                                        width = 5f,
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }
                        drawLayer(graphicsLayer)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = {
                    paths.clear()
                    currentPath = null
                    recomposeTrigger++
                }) {
                    Text(
                        text = "Clear",
                        style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                        color = Color.Red
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val bitmap = graphicsLayer.toImageBitmap()
                        val bytes = bitmap.toByteArray()
                        if (bytes != null) {
                            onSignatureSaved(bytes)
                        }
                        onDismissRequest()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
            ) {
                Text(
                    text = "Save",
                    color = Color.White,
                    style = textStyle(size = 14.sp, weight = FontWeight.Bold)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = "Cancel",
                    style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                    color = Color.Gray
                )
            }
        }
    )
}
