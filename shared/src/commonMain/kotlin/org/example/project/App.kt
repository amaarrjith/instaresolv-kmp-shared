package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.key
import androidx.compose.runtime.LaunchedEffect
import org.example.project.navigation.AppNavigation
import org.example.project.utilites.setAppLocale
import org.example.project.ui.screens.AppLanguage
import org.example.project.ui.viewmodel.GlobalSettingsViewModel
import org.koin.compose.koinInject
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun App() {
    val globalSettingsViewModel: GlobalSettingsViewModel = koinInject()
    val currentLanguage by globalSettingsViewModel.currentLanguage.collectAsState()

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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }
            ) {
                AppNavigation()
            }
            }
        }
    }
}