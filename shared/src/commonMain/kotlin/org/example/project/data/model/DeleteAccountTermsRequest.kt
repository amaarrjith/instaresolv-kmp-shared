package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountTermsRequest(
    val isGuestUser: Boolean = false,
    val userId: Int
)
