package org.example.project.domain.validation

class LoginValidator {

    fun validateEmail(email: String): String? {
        if (email.isBlank()) return "Email is required"
        if (!email.contains("@")) return "Invalid email"
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isBlank()) return "Password is required"
        if (password.length < 8) return "Minimum 8 characters"
        return null
    }
}
