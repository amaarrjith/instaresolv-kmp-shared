package org.example.project.utilites

import org.example.project.network.BASE_URL

interface AppInfo {
    val appVersion: String
    val environment: String
        get() = when {
            BASE_URL.contains("dev", ignoreCase = true) -> "Development"
            BASE_URL.contains("staging", ignoreCase = true) -> "Staging"
            else -> "Production"
        }
    val formattedVersionInfo: String
        get() = "v$appVersion ($environment)"
}

expect fun getAppInfo(): AppInfo
