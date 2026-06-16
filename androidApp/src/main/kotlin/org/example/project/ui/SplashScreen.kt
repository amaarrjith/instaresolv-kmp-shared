package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import org.example.project.R
import kotlinx.coroutines.delay
import org.example.project.splash.SplashViewModel
import org.koin.compose.koinInject


@Composable
fun SplashScreen(
    onNavigateToWelcomeScreen: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val viewModel: SplashViewModel = koinInject()
    LaunchedEffect(Unit) {
        delay(3000)
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
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_splash_background),
            contentDescription = null
        )
        SplashScreenContent()
    }
}

@Composable
fun SplashScreenContent() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_app_logo),
            contentDescription = null
        )
    }
}
