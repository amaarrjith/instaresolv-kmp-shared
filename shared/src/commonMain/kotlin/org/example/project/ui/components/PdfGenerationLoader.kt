package org.example.project.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.typography.textStyle
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PdfGenerationLoader() {
    Dialog(
        onDismissRequest = { /* Cannot dismiss */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedClockIcon()
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Generating PDF",
                        style = textStyle(size = 18.sp, weight = FontWeight.Bold),
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Please wait while the document is prepared.\nThis may take some time.",
                        style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedClockIcon(
    modifier: Modifier = Modifier.size(80.dp)
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        val strokeWidth = 8.dp.toPx()
        val radius = size.minDimension / 2f - strokeWidth / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        // Draw outer circle
        drawCircle(
            color = Color(0xFF3F5170), // Dark blueish gray
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Draw hour hand (static, pointing to roughly 4 o'clock)
        val hourAngle = PI / 6 // 30 degrees from bottom right
        val hourLength = radius * 0.5f
        val hourEndX = center.x + hourLength * cos(hourAngle).toFloat()
        val hourEndY = center.y + hourLength * sin(hourAngle).toFloat()
        
        drawLine(
            color = Color(0xFF3F5170),
            start = center,
            end = Offset(hourEndX, hourEndY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        // Draw minute hand (animated, pointing up and rotating)
        val minuteAngle = (rotationAngle - 90.0) * PI / 180.0 // Start at 12 o'clock (-90 deg)
        val minuteLength = radius * 0.7f
        val minuteEndX = center.x + minuteLength * cos(minuteAngle).toFloat()
        val minuteEndY = center.y + minuteLength * sin(minuteAngle).toFloat()
        
        drawLine(
            color = Color(0xFFE53935), // Red
            start = center,
            end = Offset(minuteEndX, minuteEndY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
@Preview
fun PdfGenerationLoaderPreview() {
    PdfGenerationLoader()
}