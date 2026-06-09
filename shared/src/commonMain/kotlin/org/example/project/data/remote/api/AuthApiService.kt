package com.example.instaresolv.data.remote.api

import com.example.instaresolv.data.model.LoginRequest
import com.example.instaresolv.data.model.LoginResponse

interface AuthApiService {

    suspend fun login(
        request: LoginRequest
    ): LoginResponse
}
