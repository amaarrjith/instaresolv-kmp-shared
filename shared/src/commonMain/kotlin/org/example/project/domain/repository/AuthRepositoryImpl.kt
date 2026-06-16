package org.example.project.domain.repository

import org.example.project.data.model.ForgetPasswordRequest
import org.example.project.data.model.ForgetPasswordResponse
import org.example.project.data.model.LoginRequest
import org.example.project.data.model.LoginResponse
import org.example.project.data.model.OTPRequest
import org.example.project.data.model.OTPResponse
import org.example.project.data.model.RegisterRequest
import org.example.project.data.model.RegisterResponse
import org.example.project.data.remote.api.AuthApiService
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AuthRepositoryImpl(
    private val apiService: AuthApiService
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): NetworkResult<LoginResponse> {
        return apiService.login(
            LoginRequest(
                email = email,
                password = password
            )
        )
    }

    override suspend fun forgetPassword(
        email: String
    ): NetworkResult<ForgetPasswordResponse> {
        return apiService.forgetPassword(
            ForgetPasswordRequest(
                email = email
            )
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        designation: String,
        company: String,
        isTermsAccepted: Boolean
    ): NetworkResult<RegisterResponse> {
        return apiService.register(
            RegisterRequest(
                uuid = Uuid.random().toString(),
                name = fullName,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                designation = designation,
                company = company
            )
        )
    }

    override suspend fun verifyOTP(
        tempUserId: Int,
        email: String,
        otp: String
    ): NetworkResult<OTPResponse> {
        return apiService.verifyOTP(
            OTPRequest(
                tempUserId = tempUserId,
                email = email,
                otp = otp
            )
        )
    }
}
