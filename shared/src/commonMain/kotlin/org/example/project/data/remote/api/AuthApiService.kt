package org.example.project.data.remote.api

import org.example.project.data.model.ForgetPasswordRequest
import org.example.project.data.model.ForgetPasswordResponse
import org.example.project.data.model.LoginRequest
import org.example.project.data.model.LoginResponse
import org.example.project.data.model.OTPRequest
import org.example.project.data.model.OTPResponse
import org.example.project.data.model.RegisterRequest
import org.example.project.data.model.RegisterResponse
import org.example.project.domain.repository.NetworkResult

interface AuthApiService {

    suspend fun login(
        request: LoginRequest
    ): NetworkResult<LoginResponse>

    suspend fun forgetPassword(
        request: ForgetPasswordRequest
    ): NetworkResult<ForgetPasswordResponse>

    suspend fun register(
        request: RegisterRequest
    ): NetworkResult<RegisterResponse>

    suspend fun verifyOTP(
        request: OTPRequest
    ): NetworkResult<OTPResponse>
}
