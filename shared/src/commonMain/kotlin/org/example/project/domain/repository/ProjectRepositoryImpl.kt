package org.example.project.domain.repository

import org.example.project.data.model.CreateProjectRequest
import org.example.project.data.model.CreateProjectResponse
import org.example.project.data.model.ProjectAccessRequest
import org.example.project.data.model.ProjectAccessResponse
import org.example.project.data.model.ProjectListRequest
import org.example.project.data.model.ProjectListResponse
import org.example.project.data.model.ViewProjectRequest
import org.example.project.data.model.ViewProjectResponse
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult

class ProjectRepositoryImpl(
    private val apiService: AuthApiService
): ProjectRepository {
    override suspend fun getProjects(
        searchKey: String,
        isProjectList: Boolean,
        isInvite: Boolean
    ): NetworkResult<ProjectListResponse> {
        return apiService.getProject(
            ProjectListRequest(
                searchKey = searchKey,
                isProjectList = isProjectList,
                isInvite = isInvite
            )
        )
    }

    override suspend fun createProject(
        groupCode: String,
        groupName: String,
        description: String,
        groupImage: String
    ): NetworkResult<CreateProjectResponse> {
        return apiService.createProject(
            CreateProjectRequest(
                groupCode = groupCode,
                groupName = groupName,
                description = description,
                groupImage = groupImage
            )
        )
    }

    override suspend fun requestProjectAccess(
        groupId: String,
        groupShortCode: String
    ): NetworkResult<ProjectAccessResponse> {
        return apiService.requestProjectAccess(
            ProjectAccessRequest(
                groupId = groupId,
                groupShortCode = groupShortCode
            )
        )
    }

    override suspend fun viewProject(
        groupId: String,
        groupShortCode: String
    ): NetworkResult<ViewProjectResponse> {
        return apiService.viewProject(
            ViewProjectRequest(
                groupId = groupId,
                groupShortCode = groupShortCode,
            )
        )
    }
}