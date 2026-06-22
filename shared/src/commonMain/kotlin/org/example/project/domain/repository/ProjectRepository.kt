package org.example.project.domain.repository
import org.example.project.data.model.CreateProjectResponse
import org.example.project.data.model.ProjectAccessResponse
import org.example.project.data.model.ProjectListResponse
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
}