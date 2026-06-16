package org.example.project.domain.repository

import org.example.project.data.model.ForgetPasswordResponse
import org.example.project.data.model.LoginResponse
import org.example.project.data.model.OTPResponse
import org.example.project.data.model.RegisterResponse

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): NetworkResult<LoginResponse>

    suspend fun forgetPassword(
        email: String
    ): NetworkResult<ForgetPasswordResponse>

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        designation: String,
        company: String,
        isTermsAccepted: Boolean
    ): NetworkResult<RegisterResponse>

    suspend fun verifyOTP(
        tempUserId: Int,
        email: String,
        otp: String
    ): NetworkResult<OTPResponse>
}
