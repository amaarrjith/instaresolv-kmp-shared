package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ForgetPasswordResponse(
    val message: String? = null
)