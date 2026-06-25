package org.example.project.data.remote.api

import org.example.project.data.model.AuthResponse
import org.example.project.data.model.CommonModelResponse
import org.example.project.data.model.CommonResponse
import org.example.project.data.model.CreateProjectRequest
import org.example.project.data.model.CreateProjectResponse
import org.example.project.data.model.DeleteProjectRequest
import org.example.project.data.model.ExitProjectRequest
import org.example.project.data.model.ForgetPasswordRequest
import org.example.project.data.model.ForgetPasswordResponse
import org.example.project.data.model.HomeContentsRequest
import org.example.project.data.model.HomeResponse
import org.example.project.data.model.ImageUploadData
import org.example.project.data.model.InviteUsersRequest
import org.example.project.data.model.LoginRequest
import org.example.project.data.model.LoginResponse
import org.example.project.data.model.NotificationListResponse
import org.example.project.data.model.OTPRequest
import org.example.project.data.model.OTPResponse
import org.example.project.data.model.ProjectAccessRequest
import org.example.project.data.model.ProjectAccessResponse
import org.example.project.data.model.PendingActionRequest
import org.example.project.data.model.PendingActionListResponse
import org.example.project.data.model.ObservationRequest
import org.example.project.data.model.ObservationData
import org.example.project.data.model.ProjectDetail
import org.example.project.data.model.ProjectDetailRequest
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
import org.example.project.data.model.FilterContentData

interface AuthApiService {

    suspend fun getFilterContent(): NetworkResult<FilterContentData>

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

    suspend fun getProjectDetails(
        request: ProjectDetailRequest
    ): NetworkResult<ProjectDetail>

    suspend fun inviteMembers(
        request: InviteUsersRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun deleteProject(
        request: DeleteProjectRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun exitProject(
        request: ExitProjectRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun createObservation(
        request: org.example.project.data.model.CreateObservationRequest
    ): NetworkResult<CommonResponse<org.example.project.data.model.CreateObservationResponse>>

    suspend fun changeMemberRole(
        request: org.example.project.data.model.ChangeRoleRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun removeMember(
        request: org.example.project.data.model.RemoveMemberRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun handoverSuperAdmin(
        request: org.example.project.data.model.HandoverSuperAdminRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun getGeneralContents(
        request: org.example.project.data.model.GeneralContentsRequest
    ): NetworkResult<org.example.project.data.model.GeneralContentsResponse>

    suspend fun changePassword(
        request: org.example.project.data.model.ChangePasswordRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun getDeleteAccountTerms(
        request: org.example.project.data.model.DeleteAccountTermsRequest
    ): NetworkResult<org.example.project.data.model.DeleteAccountTermsData>

    suspend fun getGroupUsers(
        request: org.example.project.data.model.GroupUserRequest
    ): NetworkResult<org.example.project.data.model.GroupUserResponse>

    suspend fun requestDeleteAccount(
        request: org.example.project.data.model.DeleteAccountRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun verifyDeleteAccount(
        request: org.example.project.data.model.DeleteAccountRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun sendContactMessage(
        request: org.example.project.data.model.ContactMessageRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun getPendingActions(
        request: PendingActionRequest
    ): NetworkResult<org.example.project.data.model.PendingActionData>

    suspend fun getObservationList(
        request: ObservationRequest
    ): NetworkResult<ObservationData>

    suspend fun getObservationDetail(
        request: org.example.project.data.model.ObservationDetailRequest
    ): NetworkResult<org.example.project.data.model.ObservationDetailResponse>
}
