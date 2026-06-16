package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OTPRequest(
    val tempUserId: Int,
    val email: String,
    val otp: String
)
