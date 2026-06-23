package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectDetail(
    val groupId: Int,
    val groupCode: String,
    val groupImage: String,
    val groupName: String,
    val description: String,
    val userRole: Int,
    val isAdmin: Boolean,
    val planDescription: String? = null,
    val emergencyNumber: String? = null,
    val members: List<ProjectMember>,
    val notificationUnReadCount: Int,
    val pendingActionsCount: Int,
    val trainingFileUrl: String
)

@Serializable
data class ProjectMember(
    val userId: Int,
    val image: String,
    val name: String,
    val email: String,
    val role: Int
)

@Serializable
data class ProjectDetailRequest(
    val groupId: Int,
    val groupCode: String,
    val notificationId: Int = -1
)

@Serializable
data class ChangeRoleRequest(
    val userId: Int,
    val groupId: Int,
    val groupCode: String,
    val newRole: Int
)

@Serializable
data class RemoveMemberRequest(
    val groupId: Int,
    val groupCode: String,
    val userId: Int
)