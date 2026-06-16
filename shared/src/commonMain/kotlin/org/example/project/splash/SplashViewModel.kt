package org.example.project.splash

import org.example.project.data.settings.AuthPreferences

class SplashViewModel(
    private val authPreferences: AuthPreferences
) {
    fun isLoggedIn(): Boolean {
        return authPreferences.isLoggedIn()
    }

    fun isWelcomePageShown(): Boolean {
        return authPreferences.getWelcomePageShownStatus()
    }
}