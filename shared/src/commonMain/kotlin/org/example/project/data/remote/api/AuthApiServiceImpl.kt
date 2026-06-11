package org.example.project.data.remote.api

import org.example.project.data.model.LoginRequest
import org.example.project.data.model.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.project.data.model.CommonResponse
import org.example.project.domain.repository.NetworkResult

class AuthApiServiceImpl(
    private val httpClient: HttpClient
) : AuthApiService {

    override suspend fun login(
        request: LoginRequest
    ): NetworkResult<LoginResponse> {
        return try {
            val response = httpClient.post("user/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            val body = response.body<CommonResponse<LoginResponse>>()

            if (body.hasError) {
                NetworkResult.Error(
                    body.message ?: "Something went wrong"
                )
            } else {
                NetworkResult.Success(
                    body.response ?: return NetworkResult.Error("Empty response")
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(
                e.message ?: "Something went wrong"
            )
        }
    }
}
