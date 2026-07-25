package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import org.example.project.data.settings.AuthPreferences
import org.example.project.data.settings.AppPreferences

actual fun createHttpClient(authPreferences: AuthPreferences, appPreferences: AppPreferences): HttpClient = HttpClient(Darwin) {
    commonConfig(authPreferences, appPreferences)
}
