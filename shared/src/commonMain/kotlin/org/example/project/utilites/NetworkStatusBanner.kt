package org.example.project.utilites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.typography.textStyle
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import instaresolv.shared.generated.resources.Res

enum class BannerStatus {
    Hidden,
    Offline,
    BackOnline
}

@Composable
fun WifiStatusIcon(
    isOnline: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(14.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val w = size.width
        val h = size.height
        
        // Draw the wifi-off slash line ONLY when offline
        if (!isOnline) {
            drawLine(
                color = color,
                start = Offset(2.dp.toPx(), 2.dp.toPx()),
                end = Offset(w - 2.dp.toPx(), h - 2.dp.toPx()),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
        
        // Draw outer arc
        drawArc(
            color = color,
            startAngle = 220f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(1.dp.toPx(), 2.dp.toPx()),
            size = Size(w - 2.dp.toPx(), h - 2.dp.toPx()),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Draw middle arc
        drawArc(
            color = color.copy(alpha = 0.8f),
            startAngle = 230f,
            sweepAngle = 80f,
            useCenter = false,
            topLeft = Offset(3.dp.toPx(), 4.dp.toPx()),
            size = Size(w - 6.dp.toPx(), h - 6.dp.toPx()),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Draw dot
        drawCircle(
            color = color,
            radius = 1.2.dp.toPx(),
            center = Offset(w / 2, h - 1.5.dp.toPx())
        )
    }
}

@Composable
fun NetworkStatusBanner(
    isNetworkConnected: Boolean,
    onVisibilityChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var bannerStatus by remember {
        mutableStateOf(if (isNetworkConnected) BannerStatus.Hidden else BannerStatus.Offline)
    }

    var isFirstRun by remember { mutableStateOf(true) }

    LaunchedEffect(isNetworkConnected) {
        if (isFirstRun) {
            isFirstRun = false
            // On startup, if online: stay hidden. If offline: show warning.
            if (!isNetworkConnected) {
                bannerStatus = BannerStatus.Offline
            }
            return@LaunchedEffect
        }

        if (!isNetworkConnected) {
            bannerStatus = BannerStatus.Offline
        } else {
            bannerStatus = BannerStatus.Hidden
        }
    }

    val isVisible = bannerStatus != BannerStatus.Hidden
    LaunchedEffect(isVisible) {
        onVisibilityChanged(isVisible)
    }

    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(), // Banner itself handles the status bar height padding
            color = Color.Black
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                WifiStatusIcon(
                    isOnline = false,
                    color = Color(0xFFE53935) // Red wifi-off icon when offline
                )
                
                Spacer(modifier = Modifier.padding(end = 8.dp))
                
                Text(
                    text = "Your device is offline.",
                    style = textStyle(
                        size = 13.sp,
                        weight = FontWeight.Medium
                    ),
                    color = Color.White,
                )
            }
        }
    }
}
