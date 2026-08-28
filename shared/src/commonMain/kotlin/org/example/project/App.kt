package org.example.project

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.example.project.ui.navigation.AppNavigation
import org.example.project.utilites.setAppLocale
import org.example.project.ui.viewmodel.GlobalSettingsViewModel
import org.koin.compose.koinInject
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalFocusManager
import org.example.project.network.NetworkMonitor
import org.example.project.utilites.NetworkStatusBanner

@Composable
fun App() {
    val globalSettingsViewModel: GlobalSettingsViewModel = koinInject()
    val networkMonitor: NetworkMonitor = koinInject()
    val currentLanguage by globalSettingsViewModel.currentLanguage.collectAsState()
    val isNetworkConnected by networkMonitor
        .isNetworkConnected
        .collectAsState(initial = true)
    
    val layoutDirection = if (currentLanguage.isRtl) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    LaunchedEffect(currentLanguage) {
        setAppLocale(currentLanguage.code)
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection
    ) {
        key(currentLanguage) {
        MaterialTheme {
            val focusManager = LocalFocusManager.current
            var isBannerVisible by remember { mutableStateOf(false) }

            // Smooth animations for corner radius and background color
            val animatedCornerRadius by animateDpAsState(
                targetValue = if (isBannerVisible) 24.dp else 0.dp
            )
            val animatedBgColor by animateColorAsState(
                targetValue = if (isBannerVisible) Color.Black else Color.White
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(animatedBgColor)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            focusManager.clearFocus()
                        }
                    }
            ) {
                NetworkStatusBanner(
                    isNetworkConnected = isNetworkConnected,
                    onVisibilityChanged = { isBannerVisible = it },
                    modifier = Modifier.fillMaxWidth()
                )

                // Main app navigation content container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(
                                topStart = animatedCornerRadius,
                                topEnd = animatedCornerRadius
                            )
                        )
                        .background(Color.White)
                ) {
                    AppNavigation()
                }
            }
            }
        }
    }
}
