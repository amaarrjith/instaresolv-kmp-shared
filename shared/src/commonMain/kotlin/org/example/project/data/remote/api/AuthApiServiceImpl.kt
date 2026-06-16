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
import org.example.project.data.model.ForgetPasswordRequest
import org.example.project.data.model.ForgetPasswordResponse
import org.example.project.data.model.OTPRequest
import org.example.project.data.model.OTPResponse
import org.example.project.data.model.RegisterRequest
import org.example.project.data.model.RegisterResponse
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

    override suspend fun forgetPassword(
        request: ForgetPasswordRequest
    ): NetworkResult<ForgetPasswordResponse> {
        return try {
            val response = httpClient.post("forgot-password") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            val body = response.body<CommonResponse<ForgetPasswordResponse>>()
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

    override suspend fun register(
        request: RegisterRequest
    ): NetworkResult<RegisterResponse> {
        return try {
            val response = httpClient.post("user/registration") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            val body = response.body<CommonResponse<RegisterResponse>>()
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

    override suspend fun verifyOTP(request: OTPRequest): NetworkResult<OTPResponse> {
        return try {
            val response = httpClient.post("user/email-verify") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            val body = response.body<CommonResponse<OTPResponse>>()
            if (body.hasError) {
                NetworkResult.Error(
                    body.message ?: "Something went wrong"
                )
            } else {
                if (body.response?.otpVerified ?: false) {
                    NetworkResult.Success(
                        body.response ?: return NetworkResult.Error("Empty response")
                    )
                } else {
                    NetworkResult.Error(
                        body.response?.statusMessage ?: "Something went wrong"
                    )
                }
            }
        } catch (e: Exception) {
                NetworkResult.Error(
                    e.message ?: "Something went wrong"
                )
            }
        }
}
