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
    val role: Int,
    val designation: List<Int>
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

@Serializable
data class HandoverSuperAdminRequest(
    val password: String,
    val groupId: Int,
    val groupCode: String,
    val handOverTo: Int
)

@Serializable
data class ChangeDesignationRequest(
    val groupId: Int,
    val groupCode: String,
    val userId: Int,
    val designation: List<Int>
)

@Serializable
data class ChangeDesignationResponse(
    val isSuccess: Boolean? = null,
    val statusMessage: String? = null,
    val userId: Int? = null
)