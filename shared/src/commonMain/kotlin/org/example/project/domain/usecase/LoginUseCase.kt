package com.example.instaresolv.domain.usecase

import com.example.instaresolv.data.model.LoginResponse
import com.example.instaresolv.domain.repository.AuthRepository
import com.example.instaresolv.domain.validation.LoginValidator

class LoginUseCase(
    private val repository: AuthRepository,
    private val validator: LoginValidator
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<LoginResponse> {

        validator.validateEmail(email)
            ?.let { return Result.failure(Exception(it)) }

        validator.validatePassword(password)
            ?.let { return Result.failure(Exception(it)) }

        return runCatching {
            repository.login(email, password)
        }
    }
}
