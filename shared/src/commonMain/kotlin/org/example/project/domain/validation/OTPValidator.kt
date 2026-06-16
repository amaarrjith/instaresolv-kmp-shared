package org.example.project.domain.validation

class OTPValidator {

    fun validateOTP(otp: String): String? {
        if (otp.length != 4) {
            return "OTP must be 4 characters long"
        }
        if (!otp.all { it.isDigit() }) {
            return "OTP must contain only digits"
        }
        return null
    }

}