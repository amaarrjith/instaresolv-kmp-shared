package org.example.project.data.remote.api

import org.example.project.data.model.LoginRequest
import org.example.project.data.model.LoginResponse
import org.example.project.domain.repository.NetworkResult

interface AuthApiService {

    suspend fun login(
        request: LoginRequest
    ): NetworkResult<LoginResponse>
}
