package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ForgetPasswordRequest(
    val email: String
)