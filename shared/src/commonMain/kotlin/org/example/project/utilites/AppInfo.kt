package org.example.project.utilites

import org.example.project.config.AppConfig
import org.example.project.config.AppEnvironment

interface AppInfo {
    val appVersion: String
    val environment: String
        get() = when (AppConfig.environment) {
            AppEnvironment.DEVELOPMENT -> "Development"
            AppEnvironment.STAGING -> "Staging"
            AppEnvironment.PRODUCTION -> "Production"
        }
    val formattedVersionInfo: String
        get() = "v$appVersion ($environment)"
}

expect fun getAppInfo(): AppInfo
