package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserEditResponse(
    val userId: Int,
    val email: String,
    val name: String,
    val profileImage: String,
    val company: String,
    val designation: String
)

@Serializable
data class UserEditRequest(
    val name: String,
    val profileImage: String,
    val company: String,
    val designation: String
)