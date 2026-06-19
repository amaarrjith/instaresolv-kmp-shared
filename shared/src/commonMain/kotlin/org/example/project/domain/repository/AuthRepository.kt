package org.example.project.domain.repository

import org.example.project.data.model.ForgetPasswordResponse
import org.example.project.data.model.HomeResponse
import org.example.project.data.model.LoginResponse
import org.example.project.data.model.OTPResponse
import org.example.project.data.model.RegisterResponse
import org.example.project.data.model.UserCheckoutRequest
import org.example.project.data.model.UserResponse
import org.example.project.network.NetworkResult

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

    suspend fun userCheckOut(
        uuid: String
    ): NetworkResult<UserResponse>

    suspend fun getHomeContents(
        userId: Int
    ): NetworkResult<HomeResponse>
}
