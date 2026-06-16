package org.example.project.welcomescreen

import org.example.project.data.settings.AuthPreferences

class WelcomeScreenViewModel(
    private val authPreferences: AuthPreferences
) {
    fun saveWelcomeScreenStatus() {
        authPreferences.saveWelcomePageShownStatus(true)
    }
}