package org.example.project.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import org.example.project.data.model.ForgetPasswordRequest
import org.example.project.data.model.ForgetPasswordResponse
import org.example.project.data.model.LoginRequest
import org.example.project.data.model.LoginResponse
import org.example.project.data.model.OTPRequest
import org.example.project.data.model.OTPResponse
import org.example.project.data.model.RegisterRequest
import org.example.project.data.model.RegisterResponse
import org.example.project.data.model.UserCheckoutRequest
import org.example.project.data.model.UserResponse
import org.example.project.network.ApiEndpoints
import org.example.project.network.ErrorType
import org.example.project.network.NetworkResult
import org.example.project.network.jsonBody
import org.example.project.network.safeApiCall

class AuthApiServiceImpl(
    private val httpClient: HttpClient
) : AuthApiService {

    override suspend fun login(
        request: LoginRequest
    ): NetworkResult<LoginResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.LOGIN) {
            jsonBody(request)
        }
    }

    override suspend fun forgetPassword(
        request: ForgetPasswordRequest
    ): NetworkResult<ForgetPasswordResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.FORGOT_PASSWORD) {
            jsonBody(request)
        }
    }

    override suspend fun register(
        request: RegisterRequest
    ): NetworkResult<RegisterResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.REGISTER) {
            jsonBody(request)
        }
    }

    override suspend fun verifyOTP(
        request: OTPRequest
    ): NetworkResult<OTPResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.VERIFY_OTP) {
            jsonBody(request)
        }
    }

    override suspend fun userCheckOut(request: UserCheckoutRequest): NetworkResult<UserResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.USER_CHECKOUT) {
            jsonBody(request)
        }
    }
}
