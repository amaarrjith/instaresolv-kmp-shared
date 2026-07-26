package org.example.project.utilites

import platform.Foundation.NSUserDefaults

actual fun setAppLocale(languageTag: String) {
    // For Urdu, we set the iOS system locale to Arabic ("ar") so that Core Text
    // uses Naskh script rendering for Arabic characters instead of Nastaliq.
    // Our Compose string resources are keyed by AppLanguage (values-ur is loaded
    // via key(currentLanguage) recomposition), so Urdu strings still appear correctly.
    val iosLocale = if (languageTag == "ur") "ar" else languageTag
    NSUserDefaults.standardUserDefaults.setObject(listOf(iosLocale), "AppleLanguages")
    NSUserDefaults.standardUserDefaults.synchronize()
}

