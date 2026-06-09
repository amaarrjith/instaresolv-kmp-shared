package org.example.project.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


internal const val BASE_URL = "https://instaresolv-dev.zoondia.org/api/"
internal fun HttpClientConfig<*>.commonConfig() {

    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }

    install(Logging) {

        logger = object : Logger {

            override fun log(
                message: String
            ) {
                println("KTOR => $message")
            }
        }

        level = LogLevel.ALL
    }

    defaultRequest {
        url(BASE_URL)
    }
}

expect fun createHttpClient(): HttpClient