package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_splash_background
import instaresolv.shared.generated.resources.ic_app_logo
import org.example.project.splash.SplashUiState
import org.example.project.splash.SplashViewModel
import org.koin.compose.koinInject


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
            contentDescription = null
        )
        SplashScreenContent(
            uiState.value
        )
    }
}

@Composable
fun SplashScreenContent(
    uiState: SplashUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_app_logo),
            contentDescription = null
        )
    }
}
