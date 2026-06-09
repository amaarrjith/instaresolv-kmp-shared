package org.example.project.domain.usecase

import org.example.project.data.model.LoginResponse
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.validation.LoginValidator

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
