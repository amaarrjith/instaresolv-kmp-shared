package com.example.instaresolv.domain.repository

import com.example.instaresolv.data.model.LoginRequest
import com.example.instaresolv.data.model.LoginResponse
import com.example.instaresolv.data.remote.api.AuthApiService

class AuthRepositoryImpl(
    private val apiService: AuthApiService
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): LoginResponse {
        return apiService.login(
            LoginRequest(
                email = email,
                password = password
            )
        )
    }
}
