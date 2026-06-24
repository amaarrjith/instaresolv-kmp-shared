package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import org.example.project.localization.*
import org.example.project.navigation.AppNavigation
import org.example.project.ui.screens.AppLanguage
import org.example.project.ui.viewmodel.GlobalSettingsViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    val globalSettingsViewModel: GlobalSettingsViewModel = koinInject()
    val currentLanguage by globalSettingsViewModel.currentLanguage.collectAsState()

    val layoutDirection = if (currentLanguage.isRtl) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    val appStrings = when (currentLanguage) {
        AppLanguage.ENGLISH -> EnStrings
        AppLanguage.ARABIC -> ArStrings
        AppLanguage.URDU -> UrStrings
        AppLanguage.HINDI -> HiStrings
        AppLanguage.SPANISH -> EsStrings
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        LocalAppStrings provides appStrings
    ) {
        MaterialTheme {
            AppNavigation()
        }
    }
}