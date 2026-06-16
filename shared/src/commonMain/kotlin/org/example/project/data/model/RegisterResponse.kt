package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val tempUserId: Int? = null,
    val email: String? = null,
) {
}