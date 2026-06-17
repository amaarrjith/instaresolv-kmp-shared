package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OTPResponse(
    val statusMessage: String? = null,
    val otpVerified: Boolean = false,
    val uuid: String? = null,
    val user: UserResponse? = null,
    val settings: NotificationSettingsModel? = null,
    val auth: AuthResponse? = null
)
