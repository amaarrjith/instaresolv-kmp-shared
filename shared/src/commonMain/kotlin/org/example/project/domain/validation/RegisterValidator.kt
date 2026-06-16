package org.example.project.domain.validation

class RegisterValidator {
    fun validateFullName(fullName: String): String? {
        if (fullName.isBlank()) return "Full name is required"
        return null
    }
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
    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        if (confirmPassword.isBlank()) return "Confirm password is required"
        if (password != confirmPassword) return "Passwords do not match"
        return null
    }
    fun validateTermsAndConditions(isAccepted: Boolean): String? {
        if (!isAccepted) return "Please accept the terms and conditions"
        return null
    }
}