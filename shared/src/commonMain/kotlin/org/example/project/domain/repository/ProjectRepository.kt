package org.example.project.domain.repository
import org.example.project.data.model.ProjectListResponse
import org.example.project.network.NetworkResult

interface ProjectRepository {
    suspend fun getProjects(
        searchKey: String,
        isProjectList: Boolean,
        isInvite: Boolean
    ): NetworkResult<ProjectListResponse>
}