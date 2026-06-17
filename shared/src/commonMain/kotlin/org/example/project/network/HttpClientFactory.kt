package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.data.settings.AuthPreferences

internal const val BASE_URL = "https://instaresolv-dev.zoondia.org/api/"

internal fun HttpClientConfig<*>.commonConfig(authPreferences: AuthPreferences) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        })
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis = 30_000
    }

    install(Auth) {
        bearer {
            loadTokens {
                val access = authPreferences.getAccessToken()
                val refresh = authPreferences.getRefreshToken()
                if (access != null && refresh != null) {
                    BearerTokens(access, refresh)
                } else null
            }
        }
    }

    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("KTOR => $message")
            }
        }
        level = LogLevel.ALL
    }

    defaultRequest {
        url(BASE_URL)
    }
}

expect fun createHttpClient(authPreferences: AuthPreferences): HttpClient