package org.example.project.data.remote.api

import org.example.project.data.model.LoginRequest
import org.example.project.data.model.LoginResponse

interface AuthApiService {

    suspend fun login(
        request: LoginRequest
    ): LoginResponse
}
