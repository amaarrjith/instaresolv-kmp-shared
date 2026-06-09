package com.example.instaresolv.domain.repository

import com.example.instaresolv.data.model.LoginResponse

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): LoginResponse
}
