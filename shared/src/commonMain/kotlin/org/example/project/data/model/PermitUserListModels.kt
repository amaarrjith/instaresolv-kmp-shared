package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PermitUserListRequest(
    val groupId: Int,
    val groupCode: String,
    val designationType: Int
)

@Serializable
data class PermitUserListResponse(
    val users: List<PermitUser> = emptyList()
)

@Serializable
data class PermitUser(
    val userId: Int,
    val image: String = "",
    val name: String,
    val email: String,
    val role: Int,
    val userRole: Int? = null,
    val designation: List<Int>? = null
)

// Extension to map PermitUser to GroupUser for UI component reuse
fun PermitUser.toGroupUser(): GroupUser {
    return GroupUser(
        userId = this.userId,
        name = this.name,
        email = this.email,
        image = this.image,
        role = this.role
    )
}
