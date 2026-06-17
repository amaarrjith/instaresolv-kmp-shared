package org.example.project.homescreen

import androidx.lifecycle.ViewModel
import org.example.project.data.settings.AuthPreferences
import org.example.project.domain.repository.AuthRepository

class HomeScreenViewModel(
    private val repository: AuthRepository,
    private val preferences: AuthPreferences
): ViewModel()  {
    val user = preferences.getLoggedInUser()
    val userInfo = preferences.getLoggedInUserInfo()
}