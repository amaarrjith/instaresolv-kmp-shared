package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountTermsData(
    val content: String
)
