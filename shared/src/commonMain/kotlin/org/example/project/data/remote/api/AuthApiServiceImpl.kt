package org.example.project.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import org.example.project.data.model.CommonModelResponse
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
import org.example.project.data.model.ProjectDetail
import org.example.project.data.model.ProjectDetailRequest
import org.example.project.data.model.ProjectListRequest
import org.example.project.data.model.ProjectListResponse
import org.example.project.data.model.RegisterRequest
import org.example.project.data.model.RegisterResponse
import org.example.project.data.model.UserCheckoutRequest
import org.example.project.data.model.UserEditRequest
import org.example.project.data.model.UserEditResponse
import org.example.project.data.model.UserResponse
import org.example.project.data.model.ViewProjectRequest
import org.example.project.data.model.ViewProjectResponse
import org.example.project.network.ApiEndpoints
import org.example.project.network.ErrorType
import org.example.project.network.NetworkResult
import org.example.project.network.jsonBody
import org.example.project.network.safeApiCall

class AuthApiServiceImpl(
    private val httpClient: HttpClient
) : AuthApiService {

    override suspend fun login(
        request: LoginRequest
    ): NetworkResult<LoginResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.LOGIN) {
            jsonBody(request)
        }
    }

    override suspend fun forgetPassword(
        request: ForgetPasswordRequest
    ): NetworkResult<ForgetPasswordResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.FORGOT_PASSWORD) {
            jsonBody(request)
        }
    }

    override suspend fun register(
        request: RegisterRequest
    ): NetworkResult<RegisterResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.REGISTER) {
            jsonBody(request)
        }
    }

    override suspend fun verifyOTP(
        request: OTPRequest
    ): NetworkResult<OTPResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.VERIFY_OTP) {
            jsonBody(request)
        }
    }

    override suspend fun userCheckOut(request: UserCheckoutRequest): NetworkResult<UserResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.USER_CHECKOUT) {
            jsonBody(request)
        }
    }

    override suspend fun getProject(request: ProjectListRequest): NetworkResult<ProjectListResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.PROJECT_LIST) {
            jsonBody(request)
        }
    }

    override suspend fun refreshToken(request: org.example.project.data.model.TokenRefreshRequest): NetworkResult<org.example.project.data.model.AuthResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.REFRESH_TOKEN) {
            jsonBody(request)
        }
    }

    override suspend fun getHomeContents(request: HomeContentsRequest): NetworkResult<HomeResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.HOME_CONTENT) {
            jsonBody(request)
        }
    }

    override suspend fun getNotificationList(): NetworkResult<NotificationListResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.NOTIFICATION_LIST)
    }

    override suspend fun userEdit(request: UserEditRequest): NetworkResult<UserEditResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.USER_EDIT) {
            jsonBody(request)
        }
    }

    override suspend fun createProject(request: CreateProjectRequest): NetworkResult<CreateProjectResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.CREATE_PROJECT) {
            jsonBody(request)
        }
    }

    override suspend fun viewProject(request: ViewProjectRequest): NetworkResult<ViewProjectResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.VIEW_PROJECT) {
            jsonBody(request)
        }
    }

    override suspend fun requestProjectAccess(request: ProjectAccessRequest): NetworkResult<ProjectAccessResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.REQUEST_PROJECT_ACCESS) {
            jsonBody(request)
        }
    }

    override suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        type: Int
    ): NetworkResult<ImageUploadData> = safeApiCall {
        httpClient.post(ApiEndpoints.UPLOAD_IMAGE) {
            setBody(
                io.ktor.client.request.forms.MultiPartFormDataContent(
                    io.ktor.client.request.forms.formData {
                        append("type", type.toString())
                        append("image", imageBytes, io.ktor.http.Headers.build {
                            append(io.ktor.http.HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        })
                    }
                )
            )
        }
    }

    override suspend fun getProjectDetails(request: ProjectDetailRequest
    ): NetworkResult<ProjectDetail> = safeApiCall {
        httpClient.post(ApiEndpoints.PROJECT_DETAILS){
            jsonBody(request)
        }
    }

    override suspend fun inviteMembers(request: InviteUsersRequest): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.INVITE_MEMBERS) {
            jsonBody(request)
        }
    }

    override suspend fun deleteProject(request: DeleteProjectRequest): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.DELETE_PROJECT) {
            jsonBody(request)
        }
    }

    override suspend fun exitProject(request: ExitProjectRequest): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.EXIT_PROJECT) {
            jsonBody(request)
        }
    }

    override suspend fun changeMemberRole(request: org.example.project.data.model.ChangeRoleRequest): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.CHANGE_ROLE) {
            jsonBody(request)
        }
    }

    override suspend fun removeMember(request: org.example.project.data.model.RemoveMemberRequest): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.REMOVE_MEMBER) {
            jsonBody(request)
        }
    }
}
