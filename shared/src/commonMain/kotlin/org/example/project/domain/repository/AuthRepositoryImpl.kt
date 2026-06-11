package org.example.project.domain.repository

import org.example.project.data.model.LoginRequest
import org.example.project.data.model.LoginResponse
import org.example.project.data.remote.api.AuthApiService

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
}
