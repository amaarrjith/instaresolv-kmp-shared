package org.example.project.domain.repository
import org.example.project.data.model.CommonModelResponse
import org.example.project.data.model.CreateProjectResponse
import org.example.project.data.model.ProjectAccessResponse
import org.example.project.data.model.ProjectDetail
import org.example.project.data.model.ProjectListResponse
import org.example.project.data.model.UserResponse
import org.example.project.data.model.ViewProjectResponse
import org.example.project.network.NetworkResult

interface ProjectRepository {
    suspend fun getProjects(
        searchKey: String,
        isProjectList: Boolean,
        isInvite: Boolean
    ): NetworkResult<ProjectListResponse>

    suspend fun createProject(
        groupCode: String,
        groupName: String,
        description: String,
        groupImage: String
    ): NetworkResult<CreateProjectResponse>

    suspend fun requestProjectAccess(
        groupId: String,
        groupShortCode: String
    ): NetworkResult<ProjectAccessResponse>

    suspend fun viewProject(
        groupId: String,
        groupShortCode: String
    ): NetworkResult<ViewProjectResponse>

    suspend fun getProjectDetails(
        groupId: Int,
        groupCode: String
    ): NetworkResult<ProjectDetail>

    suspend fun inviteMembers(
        groupId: Int,
        groupCode: String,
        usersEmails: List<String>
    ): NetworkResult<CommonModelResponse>

    suspend fun deleteProject(
        groupId: Int,
        groupCode: String,
        password: String
    ): NetworkResult<CommonModelResponse>

    suspend fun exitProject(
        groupCode: String,
    ): NetworkResult<CommonModelResponse>

    suspend fun changeMemberRole(
        userId: Int,
        groupId: Int,
        groupCode: String,
        newRole: Int
    ): NetworkResult<CommonModelResponse>

    suspend fun removeMember(
        groupId: Int,
        groupCode: String,
        userId: Int
    ): NetworkResult<CommonModelResponse>
}