package org.example.project.data

import kotlinx.serialization.Serializable

@Serializable
data class UserRoleCheckRequest(
    val userId: Int
)

@Serializable
data class UserRoleCheckResponse(
    val role: Int
)