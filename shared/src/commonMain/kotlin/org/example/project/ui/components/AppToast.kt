package org.example.project.utilites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_toast_success
import instaresolv.shared.generated.resources.ic_toast_alert
import instaresolv.shared.generated.resources.ic_toast_info
import instaresolv.shared.generated.resources.ic_toast_close
import org.example.project.typography.textStyle

data class ToastStyle(
    val title: String,
    val accentColor: Color,
    val backgroundColor: Color,
    val icon: DrawableResource
)

enum class ToastType {
    Success,
    Error,
    Warning,
    Info
}

fun ToastType.style(): ToastStyle =
    when (this) {
        ToastType.Success -> ToastStyle(
            title = "Success",
            accentColor = Color(0xFF22C55E),
            backgroundColor = Color(0xFFF0FDF4),
            icon = Res.drawable.ic_toast_success
        )

        ToastType.Error -> ToastStyle(
            title = "Error",
            accentColor = Color(0xFFEF4444),
            backgroundColor = Color(0xFFFEF2F2),
            icon = Res.drawable.ic_toast_alert
        )

        ToastType.Warning -> ToastStyle(
            title = "Warning",
            accentColor = Color(0xFFF59E0B),
            backgroundColor = Color(0xFFFFFBEB),
            icon = Res.drawable.ic_toast_info
        )

        ToastType.Info -> ToastStyle(
            title = "Info",
            accentColor = Color(0xFF3B82F6),
            backgroundColor = Color(0xFFEFF6FF),
            icon = Res.drawable.ic_toast_info
        )
    }

@Composable
fun ToastView(
    message: String,
    type: ToastType,
    onDismiss: () -> Unit
) {
    val style = type.style()
    LaunchedEffect(Unit) {
        delay(3000)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .padding(bottom = 40.dp)
//            .padding(horizontal = 22.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .background(
                color = style.backgroundColor,
            )
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(4.dp)
                .background(
                    color = style.accentColor,
                    shape = RoundedCornerShape(
                        topStart = 10.dp,
                        bottomStart = 10.dp
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(style.icon),
                contentDescription = null,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                style = textStyle(
                    size = 12.sp,
                    FontWeight.Medium
                ),
                color = Color(0xFF71727A)
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(Res.drawable.ic_toast_close),
                contentDescription = null,
                modifier = Modifier.clickable {
                    onDismiss()
                }
            )
        }
    }
}

@Composable
fun BoxScope.ToastHost(
    visible: Boolean,
    type: ToastType,
    message: String,
    modifier: Modifier? = null,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 40.dp)
            .then(modifier ?: Modifier),
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        ToastView(
            type = type,
            message = message,
            onDismiss = onDismiss
        )
    }
}
