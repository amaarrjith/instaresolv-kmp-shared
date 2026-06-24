package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ContactMessageRequest(
    val email: String,
    val message: String,
    val name: String
)
