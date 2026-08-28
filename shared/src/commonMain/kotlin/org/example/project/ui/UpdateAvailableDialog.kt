package org.example.project.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.example.project.typography.plusJakartaSansFontFamily

private val SplashPrimary = Color(0xFFF5831A)
private val SplashSecondary = Color(0xFFD42027)

@Composable
fun UpdateAvailableDialog(
    onUpdateNow: () -> Unit,
    onUpdateLater: (() -> Unit)? = null,
    isForceUpdate: Boolean = true,
) {
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAnimating = true
    }

    // Full screen overlay box that fills parent (SplashScreen fills screen including status/nav bar).
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isForceUpdate) {
                    onUpdateLater?.invoke()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        UpdateAvailableCard(
            isAnimating = isAnimating,
            onUpdateNow = onUpdateNow,
            onUpdateLater = onUpdateLater,
            isForceUpdate = isForceUpdate
        )
    }
}

@Composable
private fun UpdateAvailableCard(
    isAnimating: Boolean,
    onUpdateNow: () -> Unit,
    onUpdateLater: (() -> Unit)?,
    isForceUpdate: Boolean,
) {
    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0.92f,
        animationSpec = spring(
            dampingRatio = 0.78f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "dialogScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0f,
        animationSpec = tween(250),
        label = "dialogAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.88f)
            .widthIn(max = 356.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .shadow(
                elevation = 30.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color.Black.copy(alpha = 0.65f),
                spotColor = Color.Black.copy(alpha = 0.65f)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(
                Color(0xFF151619).copy(alpha = 0.96f)
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.22f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Consume clicks on the card itself to prevent dismissing
            }
            .padding(
                horizontal = 22.dp,
                vertical = 24.dp
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // MARK: - App Logo
            Image(
                painter = painterResource(Res.drawable.ic_app_logo),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(76.dp),
                contentScale = ContentScale.Fit
            )

            // MARK: - Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.update),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = plusJakartaSansFontFamily()
                    )

                    Text(
                        text = stringResource(Res.string.available),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            fontFamily = plusJakartaSansFontFamily(),
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    SplashPrimary,
                                    SplashSecondary
                                )
                            )
                        )
                    )
                }

                // Gradient underline
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SplashPrimary,
                                    SplashSecondary
                                )
                            )
                        )
                )
            }

            // MARK: - Description
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(Res.string.new_version_available),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    fontFamily = plusJakartaSansFontFamily(),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(Res.string.update_now_description),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    fontFamily = plusJakartaSansFontFamily(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }

            // MARK: - Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Update Now
                Button(
                    onClick = onUpdateNow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = SplashPrimary.copy(alpha = 0.4f),
                            spotColor = SplashPrimary.copy(alpha = 0.4f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        SplashPrimary,
                                        SplashSecondary
                                    )
                                )
                            )
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_download),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = stringResource(Res.string.update_now),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontFamily = plusJakartaSansFontFamily()
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                painter = painterResource(Res.drawable.ic_right_icon),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Optional Update Later
                if (!isForceUpdate && onUpdateLater != null) {
                    TextButton(
                        onClick = onUpdateLater,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(Res.string.update_later),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.65f),
                            fontFamily = plusJakartaSansFontFamily()
                        )
                    }
                }
            }
        }
    }
}