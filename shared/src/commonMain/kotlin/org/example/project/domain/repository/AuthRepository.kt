package org.example.project.domain.repository

import org.example.project.data.model.LoginResponse

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): LoginResponse
}
