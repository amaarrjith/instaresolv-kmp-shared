package org.example.project.utilites

import androidx.compose.runtime.Composable

expect fun setAppLocale(languageTag: String)

@Composable
expect fun ProvideAppLocale(languageTag: String, content: @Composable () -> Unit)

