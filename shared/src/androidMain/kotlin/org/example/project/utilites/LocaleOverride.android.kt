package org.example.project.utilites

import java.util.Locale

actual fun setAppLocale(languageTag: String) {
    val locale = Locale(languageTag)
    Locale.setDefault(locale)
}
