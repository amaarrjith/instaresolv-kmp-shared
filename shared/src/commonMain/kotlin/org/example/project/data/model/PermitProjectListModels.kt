package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PermitProjectListRequest(
    val searchKey: String
)

@Serializable
data class PermitProjectListResponse(
    val groups: List<PermitProject> = emptyList()
)

@Serializable
data class PermitProject(
    val groupId: Int,
    val groupName: String? = null,
    val groupImage: String? = null,
    val groupCode: String? = null,
    val userRole: Int? = null
)

// Extension to map PermitProject to Project for UI component reuse
fun PermitProject.toProject(): Project {
    return Project(
        groupId = this.groupId,
        groupName = this.groupName,
        groupImage = this.groupImage,
        groupCode = this.groupCode,
        isAdmin = false
    )
}
