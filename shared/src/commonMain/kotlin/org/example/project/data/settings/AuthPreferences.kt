package org.example.project.data.settings

import com.russhwolf.settings.Settings

class AuthPreferences(
    private val settings: Settings
) {
    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_WELCOME_PAGE_SHOWN = "is_welcome_page_shown"
    }

    fun saveLoginStatus(isLoggedIn: Boolean) {
        settings.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
    }

    fun isLoggedIn(): Boolean {
        return settings.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout() {
        settings.remove(KEY_IS_LOGGED_IN)
    }

    fun saveWelcomePageShownStatus(isWelcomePageShown: Boolean) {
        settings.putBoolean(KEY_IS_WELCOME_PAGE_SHOWN, isWelcomePageShown)
    }

    fun getWelcomePageShownStatus(): Boolean {
        return settings.getBoolean(KEY_IS_WELCOME_PAGE_SHOWN, false)
    }

}