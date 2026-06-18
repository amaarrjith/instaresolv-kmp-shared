package org.example.project.domain.repository

import org.example.project.data.model.ProjectListRequest
import org.example.project.data.model.ProjectListResponse
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

}