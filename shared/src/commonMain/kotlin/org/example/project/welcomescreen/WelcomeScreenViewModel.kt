package org.example.project.welcomescreen

import androidx.lifecycle.ViewModel
import org.example.project.data.settings.AuthPreferences

class WelcomeScreenViewModel(
    private val authPreferences: AuthPreferences
) : ViewModel() {
    fun saveWelcomeScreenStatus() {
        authPreferences.saveWelcomePageShownStatus(true)
    }
}