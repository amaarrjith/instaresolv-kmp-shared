package org.example.project.utilites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_error
import instaresolv.shared.generated.resources.ic_error_icon
import instaresolv.shared.generated.resources.ic_network_down
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.painterResource

@Composable
fun ErrorRetryView(
    errorMessage: String,
    modifier: Modifier = Modifier,
    isNetworkError: Boolean? = null,
    onRetryClick: () -> Unit
) {
    val resolveIsNetwork = isNetworkError ?: (
        errorMessage.contains("network", ignoreCase = true) ||
        errorMessage.contains("connection", ignoreCase = true) ||
        errorMessage.contains("connect", ignoreCase = true) ||
        errorMessage.contains("offline", ignoreCase = true) ||
        errorMessage.contains("timeout", ignoreCase = true) ||
        errorMessage.contains("internet", ignoreCase = true) ||
        errorMessage.contains("host", ignoreCase = true)
    )

    val imageRes = if (resolveIsNetwork) Res.drawable.ic_network_down else Res.drawable.ic_error

    Column(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(imageRes),
            contentDescription = if (resolveIsNetwork) "No Network" else "Error",
            modifier = Modifier.wrapContentSize(),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.padding(top = 14.dp))

        Text(
            text = errorMessage,
            style = textStyle(
                size = 14.sp,
                weight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center,
            color = AppColors.Primary
        )

        Spacer(modifier = Modifier.padding(top = 14.dp))

        Canvas(
            modifier = Modifier
                .size(48.dp)
                .clickable { onRetryClick() }
                .padding(10.dp)
        ) {
            val strokeWidth = 2.5.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            
            // Draw the 280-degree arc (circular arrow body)
            drawArc(
                color = AppColors.Primary,
                startAngle = 0f,
                sweepAngle = 280f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Draw the arrowhead (triangle) pointing clockwise at the end of the arc (280 degrees)
            val angleRad = (280.0 * 3.141592653589793) / 180.0
            val arrowX = center.x + radius * kotlin.math.cos(angleRad).toFloat()
            val arrowY = center.y + radius * kotlin.math.sin(angleRad).toFloat()
            
            val arrowSize = 6.dp.toPx()
            val arrowPath = Path().apply {
                moveTo(arrowX, arrowY)
                lineTo(arrowX - arrowSize, arrowY + arrowSize * 0.5f)
                lineTo(arrowX - arrowSize * 0.5f, arrowY - arrowSize)
                close()
            }
            drawPath(
                path = arrowPath,
                color = AppColors.Primary
            )
        }
    }
}

@Composable
@Preview
fun ErrorRetryViewPreview() {
    ErrorRetryView(
        errorMessage = "Something went wrong",
        onRetryClick = {}
    )
}