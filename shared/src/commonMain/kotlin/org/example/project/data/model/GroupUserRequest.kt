package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupUserRequest(
    val searchKey: String = "",
    val groupId: Int,
    val groupCode: String
)

@Serializable
data class GroupUserResponse(
    val users: List<GroupUser>
)

@Serializable
data class GroupUser(
    val userId: Int,
    val name: String,
    val email: String,
    val image: String,
    val role: Int
)
