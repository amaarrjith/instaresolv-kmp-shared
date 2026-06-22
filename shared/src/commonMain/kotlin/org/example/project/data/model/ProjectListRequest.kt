package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProjectListRequest(
    val searchKey: String,
    val isProjectList: Boolean,
    val isInvite: Boolean
)

@Serializable
data class ProjectListResponse(
    val groups: List<Project>
)
@Serializable
data class Project(
    val groupId: Int,
    val groupName: String?,
    val groupImage: String?,
    val groupCode: String?,
    val description: String? = null
)