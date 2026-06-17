package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserCheckoutRequest(
    val uuid: String
) {
}