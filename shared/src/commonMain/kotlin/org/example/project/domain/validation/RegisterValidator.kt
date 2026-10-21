package org.example.project.domain.validation

import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.getString

class RegisterValidator {
    suspend fun validateFullName(fullName: String): String? {
        if (fullName.isBlank()) return getString(Res.string.error_full_name_required)
        return null
    }
    suspend fun validateEmail(email: String): String? {
        if (email.isBlank()) return getString(Res.string.error_email_required)
        if (!email.contains("@")) return getString(Res.string.error_invalid_email)
        return null
    }
    suspend fun validatePassword(password: String): String? {
        if (password.isBlank()) return getString(Res.string.error_password_required)
        if (password.length < 8) return getString(Res.string.error_password_at_least_8_characters)
        return null
    }
    suspend fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        if (confirmPassword.isBlank()) return getString(Res.string.error_confirm_password_required)
        if (password != confirmPassword) return getString(Res.string.passwords_do_not_match)
        return null
    }
    suspend fun validateTermsAndConditions(isAccepted: Boolean): String? {
        if (!isAccepted) return getString(Res.string.error_accept_terms)
        return null
    }
}