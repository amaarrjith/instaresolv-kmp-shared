package org.example.project.profile

import androidx.lifecycle.ViewModel
import org.example.project.data.settings.AuthPreferences

class ProfileViewModel(
    private val preferences: AuthPreferences
): ViewModel() {
    fun logout() {
        preferences.logout()
    }
}