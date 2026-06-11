package org.example.project.domain.usecase

import org.example.project.data.model.LoginResponse
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.NetworkResult
import org.example.project.domain.validation.LoginValidator

class LoginUseCase(
    private val repository: AuthRepository,
    private val validator: LoginValidator
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): NetworkResult<LoginResponse> {

        validator.validateEmail(email)
            ?.let { return NetworkResult.Error(it) }

        validator.validatePassword(password)
            ?.let { return NetworkResult.Error(it) }

        return repository.login(email, password)
    }
}
