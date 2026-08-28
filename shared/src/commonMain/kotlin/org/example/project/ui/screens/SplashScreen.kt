package org.example.project.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_splash_background
import instaresolv.shared.generated.resources.ic_app_logo
import kotlinx.coroutines.delay
import org.example.project.splash.SplashUiState
import org.example.project.splash.SplashViewModel
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import org.example.project.getPlatform
import instaresolv.shared.generated.resources.*
import org.example.project.typography.textStyle


@Composable
fun SplashScreen(
    onNavigateToWelcomeScreen: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val viewModel: SplashViewModel = koinInject()
    val uiState = viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.value.loadingCompleted) {
        if (uiState.value.loadingCompleted) {
            if (viewModel.isWelcomePageShown()) {
                if (viewModel.isLoggedIn()) {
                    onNavigateToHome()
                } else {
                    onNavigateToLogin()
                }
            } else {
                onNavigateToWelcomeScreen()
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_splash_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        SplashScreenContent(
            uiState.value
        ) {
            viewModel.onRetry()
        }
    }
}

@Composable
fun SplashScreenContent(
    uiState: SplashUiState,
    onRetry: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    Box() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_app_logo),
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                uiState.isLoading -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .width(120.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage,
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Normal
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onRetry,
                        border = BorderStroke(1.dp, Color.White),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(text = stringResource(Res.string.tryAgain))
                    }
                }
            }
        }

        if (uiState.isUpdateAvailable) {
            UpdateAvailableDialog(
                onUpdateNow = {
                    val platformName = getPlatform().name
                    val isIos = platformName.contains("iOS", ignoreCase = true)
                    val storeUrl = if (isIos) {
                        "itms-apps://itunes.apple.com/app/id6478951234"
                    } else {
                        "market://details?id=com.instaresolve.app"
                    }
                    val fallbackUrl = if (isIos) {
                        "https://apps.apple.com/app/id6478951234"
                    } else {
                        "https://play.google.com/store/apps/details?id=com.instaresolve.app"
                    }
                    try {
                        uriHandler.openUri(storeUrl)
                    } catch (e: Exception) {
                        try {
                            uriHandler.openUri(fallbackUrl)
                        } catch (fallbackEx: Exception) {
                            println("Failed to open store link: ${fallbackEx.message}")
                        }
                    }
                }
            )
        }
    }
}
