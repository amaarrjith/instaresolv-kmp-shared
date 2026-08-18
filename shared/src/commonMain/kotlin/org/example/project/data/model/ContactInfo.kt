package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ContactInfo(
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null
)
