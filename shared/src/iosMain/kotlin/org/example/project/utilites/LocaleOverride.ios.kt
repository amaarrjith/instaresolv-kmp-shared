package org.example.project.utilites

import platform.Foundation.NSUserDefaults

actual fun setAppLocale(languageTag: String) {
    NSUserDefaults.standardUserDefaults.setObject(listOf(languageTag), "AppleLanguages")
    NSUserDefaults.standardUserDefaults.synchronize()
}
