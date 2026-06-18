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
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.data.model.AuthResponse
import org.example.project.data.model.CommonResponse
import org.example.project.data.model.TokenRefreshRequest
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
            refreshTokens {
                val refreshToken = authPreferences.getRefreshToken() ?: return@refreshTokens null
                try {
                    val response = client.post(BASE_URL + ApiEndpoints.REFRESH_TOKEN) {
                        contentType(ContentType.Application.Json)
                        setBody(TokenRefreshRequest(refreshToken))
                        markAsRefreshTokenRequest()
                    }
                    if (response.status.isSuccess()) {
                        val body = response.body<CommonResponse<AuthResponse>>()
                        if (!body.hasError && body.response != null) {
                            val newAccess = body.response.access
                            val newRefresh = body.response.refresh ?: refreshToken
                            val newExpiry = body.response.tokenExpiry ?: 0L
                            if (newAccess != null) {
                                authPreferences.saveTokens(newAccess, newRefresh, newExpiry)
                                return@refreshTokens BearerTokens(newAccess, newRefresh)
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("KTOR => Token refresh failed: ${e.message}")
                }
                authPreferences.logout()
                null
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