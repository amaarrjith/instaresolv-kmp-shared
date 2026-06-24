package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountRequest(
    val guestUserId: Int = -1,
    val otp: String = "",
    val password: String = "",
    val refresh: String = ""
)
