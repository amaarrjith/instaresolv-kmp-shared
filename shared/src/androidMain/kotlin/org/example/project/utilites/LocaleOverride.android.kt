package org.example.project.utilites

import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

actual fun setAppLocale(languageTag: String) {
    val locale = Locale(languageTag)
    Locale.setDefault(locale)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
    }
    try {
        val context = org.koin.mp.KoinPlatform.getKoin().getOrNull<android.content.Context>()
        context?.let { ctx ->
            val res = ctx.resources
            val config = Configuration(res.configuration)
            config.setLocale(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocales(LocaleList(locale))
            }
            res.updateConfiguration(config, res.displayMetrics)
            val appRes = ctx.applicationContext.resources
            appRes.updateConfiguration(config, appRes.displayMetrics)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val localeManager = ctx.getSystemService(android.app.LocaleManager::class.java)
                localeManager?.applicationLocales = LocaleList.forLanguageTags(languageTag)
            }
        }
    } catch (_: Exception) {}
}

@Composable
actual fun ProvideAppLocale(languageTag: String, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val newLocale = remember(languageTag) { Locale(languageTag) }

    val localizedConfig = remember(configuration, newLocale) {
        Configuration(configuration).apply {
            setLocale(newLocale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val localeList = LocaleList(newLocale)
                LocaleList.setDefault(localeList)
                setLocales(localeList)
            }
        }
    }

    DisposableEffect(localizedConfig, newLocale) {
        Locale.setDefault(newLocale)
        val res = context.resources
        res.updateConfiguration(localizedConfig, res.displayMetrics)
        val appRes = context.applicationContext.resources
        appRes.updateConfiguration(localizedConfig, appRes.displayMetrics)
        onDispose {}
    }

    // Keep JVM default locale in sync during recompositions (in case an external Activity reset it)
    if (Locale.getDefault().language != newLocale.language) {
        Locale.setDefault(newLocale)
    }

    CompositionLocalProvider(
        LocalConfiguration provides localizedConfig
    ) {
        content()
    }
}

