package org.example.project.di

import org.example.project.di.initKoin
import org.example.project.data.settings.AuthPreferences
import org.koin.mp.KoinPlatform
import org.example.project.manager.AppManager

object KoinInitializer {
    fun initialize() {
        initKoin()
    }

    fun saveFCMToken(token: String) {
        val authPreferences = KoinPlatform.getKoin().get<AuthPreferences>()
        authPreferences.saveFCMToken(token)
    }

    fun getFCMToken(): String? {
        val authPreferences = KoinPlatform.getKoin().get<AuthPreferences>()
        return authPreferences.getFCMToken()
    }

    fun handleNotificationTap(type: Int, contentId: Int, groupCode: String?) {
        val appManager = KoinPlatform.getKoin().get<AppManager>()
        appManager.handleNotificationTap(type, contentId, groupCode)
    }
}