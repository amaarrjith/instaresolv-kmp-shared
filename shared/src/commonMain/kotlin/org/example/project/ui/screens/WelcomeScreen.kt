package org.example.project.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.bg_welcome_screen
import instaresolv.shared.generated.resources.ic_app_login_logo
import instaresolv.shared.generated.resources.ic_arrow_left
import instaresolv.shared.generated.resources.ic_audit_inspection
import instaresolv.shared.generated.resources.ic_observations
import instaresolv.shared.generated.resources.ic_right_icon
import instaresolv.shared.generated.resources.ic_toast_success
import org.example.project.typography.textStyle
import org.example.project.welcomescreen.WelcomeScreenViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
) {
    val viewModel: WelcomeScreenViewModel = koinInject()
    var currentIndex by remember { mutableStateOf(0) }
    
    val descriptions = listOf(
        stringResource(Res.string.loremIpsum),
        stringResource(Res.string.loremIpsum),
        stringResource(Res.string.loremIpsum)
    )

    val primaryColor = Color(0xFFD32F2F) // Red accent
    val bgColor = Color.White
    val platformColor = Color(0xFFF0F0F0) // Light platform for white background

    Box(
        modifier = Modifier
            .background(bgColor)
            .statusBarsPadding()
            .fillMaxSize()

    ) {
        // Red abstract background decorations
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Subtle Red Glow in the center background
            drawCircle(
                color = primaryColor.copy(alpha = 0.08f),
                radius = 320.dp.toPx() / 2,
                center = Offset(-80.dp.toPx() + size.width / 2, -50.dp.toPx() + size.height / 2)
            )
            
            // Bottom-right Red Triangle
            val trianglePath = Path().apply {
                moveTo(size.width - 180.dp.toPx(), size.height)
                lineTo(size.width, size.height - 180.dp.toPx())
                lineTo(size.width, size.height)
                close()
            }
            drawPath(
                path = trianglePath,
                color = primaryColor.copy(alpha = 0.4f),
                style = Fill
            )
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Dynamic slide content: Headers, Logo & Text
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = stringResource(Res.string.track),
                    color = Color.Black,
                    style = textStyle(size = 32.sp, weight = FontWeight.Bold)
                )
                Text(
                    text = stringResource(Res.string.resolve),
                    color = Color.Black,
                    style = textStyle(size = 32.sp, weight = FontWeight.Bold)
                )
                Text(
                    text = stringResource(Res.string.stayCompliant),
                    color = primaryColor,
                    style = textStyle(size = 32.sp, weight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(14.dp))

                AnimatedContent(targetState = currentIndex, label = stringResource(Res.string.descriptionanimation)) { targetIndex ->
                    Text(
                        text = descriptions[targetIndex],
                        color = Color.Black.copy(alpha = 0.6f),
                        style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                        lineHeight = 18.sp,
                        modifier = Modifier.heightIn(min = 52.dp, max = 72.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Middle Graphic
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(Res.drawable.ic_app_login_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Three Columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                ColumnView(
                    title = stringResource(Res.string.track1),
                    desc = "Capture issues\nin real-time",
                    icon = Res.drawable.ic_observations,
                    isActive = currentIndex == 0,
                    primaryColor = primaryColor,
                    onClick = { currentIndex = 0 },
                    modifier = Modifier.weight(1f)
                )
                
                Divider(
                    color = Color.Black.copy(alpha = 0.1f),
                    modifier = Modifier
                        .height(50.dp)
                        .width(1.dp)
                )
                
                ColumnView(
                    title = stringResource(Res.string.resolve1),
                    desc = "Take action and\nclose faster",
                    icon = Res.drawable.ic_toast_success,
                    isActive = currentIndex == 1,
                    primaryColor = primaryColor,
                    onClick = { currentIndex = 1 },
                    modifier = Modifier.weight(1f)
                )

                Divider(
                    color = Color.Black.copy(alpha = 0.1f),
                    modifier = Modifier
                        .height(50.dp)
                        .width(1.dp)
                )
                
                ColumnView(
                    title = stringResource(Res.string.comply),
                    desc = "Ensure compliance\nwith ease",
                    icon = Res.drawable.ic_audit_inspection,
                    isActive = currentIndex == 2,
                    primaryColor = primaryColor,
                    onClick = { currentIndex = 2 },
                    modifier = Modifier.weight(1f)
                )
            }

            // Page Indicator Dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                descriptions.indices.forEach { index ->
                    val isActive = index == currentIndex
                    val scale by animateFloatAsState(
                        targetValue = if (isActive) 1.25f else 1.0f,
                        label = stringResource(Res.string.dotscaleanimation)
                    )
                    
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(8.dp)
                            .scale(scale)
                            .background(
                                color = if (isActive) primaryColor else Color.Black.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (currentIndex > 0) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .background(platformColor, RoundedCornerShape(12.dp))
                            .clickable {
                                currentIndex -= 1
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_left),
                            contentDescription = stringResource(Res.string.back),
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.back),
                            color = Color.Black,
                            style = textStyle(size = 16.sp, weight = FontWeight.Bold)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .background(primaryColor, RoundedCornerShape(12.dp))
                        .clickable {
                            if (currentIndex < descriptions.size - 1) {
                                currentIndex += 1
                            } else {
                                viewModel.saveWelcomeScreenStatus()
                                onNavigateToLogin()
                            }
                        },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.continueAction),
                        color = Color.White,
                        style = textStyle(size = 16.sp, weight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(Res.drawable.ic_right_icon),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnView(
    title: String,
    desc: String,
    icon: DrawableResource,
    isActive: Boolean,
    primaryColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    
    Column(
        modifier = modifier
            .alpha(if (isActive) 1.0f else 0.6f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    color = if (isActive) primaryColor.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.06f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = if (isActive) primaryColor else Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Text(
            text = title,
            color = if (isActive) Color.Black else Color.Black.copy(alpha = 0.5f),
            style = textStyle(size = 12.sp, weight = FontWeight.Bold)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = desc,
            color = Color.Black.copy(alpha = if (isActive) 0.6f else 0.35f),
            style = textStyle(size = 10.sp, weight = FontWeight.Normal),
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}
