package org.example.project.data.remote.api

import org.example.project.data.model.AuthResponse
import org.example.project.data.model.CommonResponse
import org.example.project.data.model.CreateProjectRequest
import org.example.project.data.model.CreateProjectResponse
import org.example.project.data.model.ForgetPasswordRequest
import org.example.project.data.model.ForgetPasswordResponse
import org.example.project.data.model.HomeContentsRequest
import org.example.project.data.model.HomeResponse
import org.example.project.data.model.ImageUploadData
import org.example.project.data.model.LoginRequest
import org.example.project.data.model.LoginResponse
import org.example.project.data.model.NotificationListResponse
import org.example.project.data.model.OTPRequest
import org.example.project.data.model.OTPResponse
import org.example.project.data.model.ProjectAccessRequest
import org.example.project.data.model.ProjectAccessResponse
import org.example.project.data.model.ProjectListRequest
import org.example.project.data.model.ProjectListResponse
import org.example.project.data.model.RegisterRequest
import org.example.project.data.model.RegisterResponse
import org.example.project.data.model.TokenRefreshRequest
import org.example.project.data.model.UserCheckoutRequest
import org.example.project.data.model.UserEditRequest
import org.example.project.data.model.UserEditResponse
import org.example.project.data.model.UserResponse
import org.example.project.data.model.ViewProjectRequest
import org.example.project.data.model.ViewProjectResponse
import org.example.project.network.NetworkResult

interface AuthApiService {

    suspend fun login(
        request: LoginRequest
    ): NetworkResult<LoginResponse>

    suspend fun forgetPassword(
        request: ForgetPasswordRequest
    ): NetworkResult<ForgetPasswordResponse>

    suspend fun register(
        request: RegisterRequest
    ): NetworkResult<RegisterResponse>

    suspend fun verifyOTP(
        request: OTPRequest
    ): NetworkResult<OTPResponse>

    suspend fun userCheckOut(
        request: UserCheckoutRequest
    ): NetworkResult<UserResponse>

    suspend fun getProject(
        request: ProjectListRequest
    ): NetworkResult<ProjectListResponse>

    suspend fun refreshToken(
        request: TokenRefreshRequest
    ): NetworkResult<AuthResponse>

    suspend fun getHomeContents(
        request: HomeContentsRequest
    ): NetworkResult<HomeResponse>

    suspend fun getNotificationList(): NetworkResult<NotificationListResponse>

    suspend fun userEdit(
        request: UserEditRequest
    ): NetworkResult<UserEditResponse>

    suspend fun createProject(
        request: CreateProjectRequest
    ): NetworkResult<CreateProjectResponse>

    suspend fun viewProject(
        request: ViewProjectRequest
    ): NetworkResult<ViewProjectResponse>

    suspend fun requestProjectAccess(
        request: ProjectAccessRequest
    ): NetworkResult<ProjectAccessResponse>

    suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        type: Int
    ): NetworkResult<ImageUploadData>
}
