package org.example.project.data.settings

import com.russhwolf.settings.Settings

class AppPreferences(
    private val settings: Settings
) {
    companion object {
        private const val KEY_APP_LANGUAGE = "app_language"
    }

    fun saveLanguage(languageCode: String) {
        settings.putString(KEY_APP_LANGUAGE, languageCode)
    }

    fun getLanguage(): String {
        return settings.getString(KEY_APP_LANGUAGE, "en")
    }
}
