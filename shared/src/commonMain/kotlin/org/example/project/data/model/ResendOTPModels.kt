package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ResendOTPRequest(
    val tempUserId: Int,
    val email: String
)

@Serializable
data class ResendOTPResponse(
    val statusMessage: String? = null
)
