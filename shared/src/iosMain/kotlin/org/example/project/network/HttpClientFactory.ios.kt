package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import org.example.project.data.settings.AuthPreferences

actual fun createHttpClient(authPreferences: AuthPreferences): HttpClient = HttpClient(Darwin) {
    commonConfig(authPreferences)
}
