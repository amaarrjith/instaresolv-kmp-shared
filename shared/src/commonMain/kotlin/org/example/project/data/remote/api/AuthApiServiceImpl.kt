package com.example.instaresolv.data.remote.api

import com.example.instaresolv.data.model.LoginRequest
import com.example.instaresolv.data.model.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApiServiceImpl(
    private val httpClient: HttpClient
) : AuthApiService {

    override suspend fun login(
        request: LoginRequest
    ): LoginResponse {

        try {

            val response = httpClient.post("user/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            println("Status: ${response.status}")

            return response.body()

        } catch (e: Exception) {

            println("API ERROR: ${e.message}")
            e.printStackTrace()

            throw e
        }
    }
}
