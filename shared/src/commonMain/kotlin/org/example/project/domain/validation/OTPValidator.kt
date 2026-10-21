package org.example.project.domain.validation

import instaresolv.shared.generated.resources.*
import org.jetbrains.compose.resources.getString

class OTPValidator {

    suspend fun validateOTP(otp: String): String? {
        if (otp.length != 4) {
            return getString(Res.string.error_otp_4_characters)
        }
        if (!otp.all { it.isDigit() }) {
            return getString(Res.string.error_otp_only_digits)
        }
        return null
    }

}