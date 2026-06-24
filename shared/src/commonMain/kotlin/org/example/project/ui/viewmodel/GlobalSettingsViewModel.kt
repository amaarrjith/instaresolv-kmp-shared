package org.example.project.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.project.data.settings.AppPreferences
import org.example.project.ui.screens.AppLanguage

class GlobalSettingsViewModel(
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _currentLanguage = MutableStateFlow(
        AppLanguage.entries.find { it.code == appPreferences.getLanguage() } ?: AppLanguage.ENGLISH
    )
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    fun changeLanguage(language: AppLanguage) {
        appPreferences.saveLanguage(language.code)
        _currentLanguage.value = language
    }
}
