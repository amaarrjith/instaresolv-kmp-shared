package org.example.project.domain.repository

import org.example.project.data.model.CommonModelResponse
import org.example.project.data.model.CreateProjectRequest
import org.example.project.data.model.CreateProjectResponse
import org.example.project.data.model.DeleteProjectRequest
import org.example.project.data.model.EmployeeData
import org.example.project.data.model.ExitProjectRequest
import org.example.project.data.model.InviteUsersRequest
import org.example.project.data.model.ProjectAccessRequest
import org.example.project.data.model.ProjectAccessResponse
import org.example.project.data.model.ProjectDetail
import org.example.project.data.model.ProjectDetailRequest
import org.example.project.data.model.ProjectListRequest
import org.example.project.data.model.ProjectListResponse
import org.example.project.data.model.UserResponse
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

    override suspend fun getProjectDetails(
        groupId: Int,
        groupCode: String
    ): NetworkResult<ProjectDetail> {
        return apiService.getProjectDetails(
            ProjectDetailRequest(
                groupId = groupId,
                groupCode = groupCode
            )
        )
    }

    override suspend fun inviteMembers(
        groupId: Int,
        groupCode: String,
        usersEmails: List<String>
    ): NetworkResult<CommonModelResponse> {
        return apiService.inviteMembers(
            InviteUsersRequest(
                groupId = groupId,
                groupCode = groupCode,
                usersEmails = usersEmails
            )
        )
    }

    override suspend fun deleteProject(
        groupId: Int,
        groupCode: String,
        password: String
    ): NetworkResult<CommonModelResponse> {
        return apiService.deleteProject(
            DeleteProjectRequest(
                groupId = groupId,
                groupCode = groupCode,
                password = password
            )
        )
    }

    override suspend fun exitProject(groupCode: String): NetworkResult<CommonModelResponse> {
        return apiService.exitProject(
            ExitProjectRequest(
                groupCode = groupCode
            )
        )
    }

    override suspend fun changeMemberRole(
        userId: Int,
        groupId: Int,
        groupCode: String,
        newRole: Int
    ): NetworkResult<CommonModelResponse> {
        return apiService.changeMemberRole(
            org.example.project.data.model.ChangeRoleRequest(
                userId = userId,
                groupId = groupId,
                groupCode = groupCode,
                newRole = newRole
            )
        )
    }

    override suspend fun removeMember(
        groupId: Int,
        groupCode: String,
        userId: Int
    ): NetworkResult<CommonModelResponse> {
        return apiService.removeMember(
            org.example.project.data.model.RemoveMemberRequest(
                groupId = groupId,
                groupCode = groupCode,
                userId = userId
            )
        )
    }

    override suspend fun handoverSuperAdmin(
        password: String,
        groupId: Int,
        groupCode: String,
        handOverTo: Int
    ): NetworkResult<CommonModelResponse> {
        return apiService.handoverSuperAdmin(
            org.example.project.data.model.HandoverSuperAdminRequest(
                password = password,
                groupId = groupId,
                groupCode = groupCode,
                handOverTo = handOverTo
            )
        )
    }

    override suspend fun getGroupUsers(
        groupId: Int,
        groupCode: String,
        searchKey: String
    ): NetworkResult<org.example.project.data.model.GroupUserResponse> {
        return apiService.getGroupUsers(
            org.example.project.data.model.GroupUserRequest(
                searchKey = searchKey,
                groupId = groupId,
                groupCode = groupCode
            )
        )
    }

    override suspend fun getEmployeeList(
        groupId: String,
        pageNumber: Int,
        searchKey: String
    ): NetworkResult<List<EmployeeData>> {
        return apiService.getEmployeeList(
            org.example.project.data.model.EmployeeListRequest(
                pageNumber = pageNumber,
                searchKey = searchKey,
                groupId = groupId,
                sortType = 1
            )
        )
    }
}