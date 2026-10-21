package org.example.project.domain.validation

import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.getString

class LoginValidator {

    suspend fun validateEmail(email: String): String? {
        if (email.isBlank()) return getString(Res.string.error_email_required)
        if (!email.contains("@")) return getString(Res.string.error_invalid_email)
        return null
    }

    suspend fun validatePassword(password: String): String? {
        if (password.isBlank()) return getString(Res.string.error_password_required)
        if (password.length < 8) return getString(Res.string.error_minimum_8_characters)
        return null
    }
}
