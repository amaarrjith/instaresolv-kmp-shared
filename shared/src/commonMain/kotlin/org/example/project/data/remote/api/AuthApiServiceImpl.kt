package org.example.project.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import org.example.project.data.model.CommonModelResponse
import org.example.project.data.model.CreateProjectRequest
import org.example.project.data.model.CreateProjectResponse
import org.example.project.data.model.DeleteProjectRequest
import org.example.project.data.model.EmployeeData
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

import org.example.project.data.model.FilterContentData
import org.example.project.data.model.EmployeeListRequest

class AuthApiServiceImpl(
    private val httpClient: HttpClient
) : AuthApiService {

    override suspend fun getFilterContent(): NetworkResult<FilterContentData> = safeApiCall {
        httpClient.post(ApiEndpoints.FILTER_CONTENT)
    }

    override suspend fun getEmployeeList(request: EmployeeListRequest): NetworkResult<List<EmployeeData>> = safeApiCall {
        httpClient.post(ApiEndpoints.EMPLOYEE_LIST) {
            jsonBody(request)
        }
    }

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

    override suspend fun handoverSuperAdmin(request: org.example.project.data.model.HandoverSuperAdminRequest): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.HANDOVER_SUPERADMIN) {
            jsonBody(request)
        }
    }



    override suspend fun generateIncidentPdf(request: org.example.project.data.model.GenerateIncidentPdfRequest): NetworkResult<org.example.project.data.model.CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.GENERATE_INCIDENT_PDF) {
            jsonBody(request)
        }
    }

    override suspend fun generateObservationPdf(request: org.example.project.data.model.GenerateObservationPdfRequest): NetworkResult<org.example.project.data.model.CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.GENERATE_OBSERVATION_PDF) {
            jsonBody(request)
        }
    }

    override suspend fun generateViolationPdf(request: org.example.project.data.model.GenerateViolationPdfRequest): NetworkResult<org.example.project.data.model.CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.GENERATE_VIOLATION_PDF) {
            jsonBody(request)
        }
    }
    
    override suspend fun changePassword(request: org.example.project.data.model.ChangePasswordRequest): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.CHANGE_PASSWORD) {
            jsonBody(request)
        }
    }

    override suspend fun getGeneralContents(request: org.example.project.data.model.GeneralContentsRequest): NetworkResult<org.example.project.data.model.GeneralContentsResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.GENERAL_CONTENTS) {
            jsonBody(request)
        }
    }

    override suspend fun getDeleteAccountTerms(request: org.example.project.data.model.DeleteAccountTermsRequest): NetworkResult<org.example.project.data.model.DeleteAccountTermsData> = safeApiCall {
        httpClient.post(ApiEndpoints.DELETE_ACCOUNT_TERMS) {
            jsonBody(request)
        }
    }

    override suspend fun requestDeleteAccount(request: org.example.project.data.model.DeleteAccountRequest): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.DELETE_ACCOUNT) {
            jsonBody(request)
        }
    }

    override suspend fun verifyDeleteAccount(request: org.example.project.data.model.DeleteAccountRequest): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.DELETE_ACCOUNT_VERIFY) {
            jsonBody(request)
        }
    }

    override suspend fun sendContactMessage(request: org.example.project.data.model.ContactMessageRequest): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.CONTACT_SEND_MESSAGE) {
            jsonBody(request)
        }
    }

    override suspend fun getPendingActions(request: org.example.project.data.model.PendingActionRequest): NetworkResult<org.example.project.data.model.PendingActionData> = safeApiCall {
        httpClient.post(ApiEndpoints.PENDING_ACTION_LIST) {
            jsonBody(request)
        }
    }

    override suspend fun getObservationList(request: org.example.project.data.model.ObservationRequest): NetworkResult<org.example.project.data.model.ObservationData> = safeApiCall {
        httpClient.post(ApiEndpoints.OBSERVATION_LIST) {
            jsonBody(request)
        }
    }

    override suspend fun getGroupUsers(request: org.example.project.data.model.GroupUserRequest): NetworkResult<org.example.project.data.model.GroupUserResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.GROUP_USER_LIST) {
            jsonBody(request)
        }
    }

    override suspend fun createObservation(
        request: org.example.project.data.model.CreateObservationRequest
    ): NetworkResult<org.example.project.data.model.CommonResponse<org.example.project.data.model.CreateObservationResponse>> = safeApiCall {
        httpClient.post(ApiEndpoints.CREATE_OBSERVATION) {
            jsonBody(request)
        }
    }

    override suspend fun getObservationDetail(
        request: org.example.project.data.model.ObservationDetailRequest
    ): NetworkResult<org.example.project.data.model.ObservationDetailResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.OBSERVATION_DETAIL) {
            jsonBody(request)
        }
    }

    override suspend fun closeObservation(
        request: org.example.project.data.model.CloseObservationRequest
    ): NetworkResult<kotlinx.serialization.json.JsonObject> = safeApiCall {
        httpClient.post(ApiEndpoints.CLOSE_OBSERVATION) {
            jsonBody(request)
        }
    }

    override suspend fun getIncidentList(
        request: org.example.project.data.model.IncidentRequest
    ): NetworkResult<List<org.example.project.data.model.IncidentData>> = safeApiCall {
        httpClient.post(ApiEndpoints.INCIDENT_LIST) {
            jsonBody(request)
        }
    }

    override suspend fun addIncident(
        request: org.example.project.data.model.AddIncidentRequest
    ): NetworkResult<org.example.project.data.model.AddIncidentData> = safeApiCall {
        httpClient.post(ApiEndpoints.ADD_INCIDENT) {
            jsonBody(request)
        }
    }

    override suspend fun getIncidentDetail(
        request: org.example.project.data.model.IncidentDetailRequest
    ): NetworkResult<org.example.project.data.model.IncidentDetailResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.INCIDENT_DETAIL) {
            jsonBody(request)
        }
    }

    override suspend fun getViolationList(
        request: org.example.project.data.model.ViolationListRequest
    ): NetworkResult<List<org.example.project.data.model.ViolationData>> = safeApiCall {
        httpClient.post(ApiEndpoints.VIOLATION_LIST) {
            jsonBody(request)
        }
    }

    override suspend fun getViolationDetail(
        request: org.example.project.data.model.ViolationDetailRequest
    ): NetworkResult<org.example.project.data.model.ViolationData> = safeApiCall {
        httpClient.post(ApiEndpoints.VIOLATION_DETAIL) {
            jsonBody(request)
        }
    }

    override suspend fun getInspectionList(
        request: org.example.project.data.model.InspectionListRequest
    ): NetworkResult<List<org.example.project.data.model.InspectionData>> = safeApiCall {
        httpClient.post(ApiEndpoints.INSPECTION_LIST) {
            jsonBody(request)
        }
    }

    override suspend fun getPreTaskList(
        request: org.example.project.data.model.PreTaskListRequest
    ): NetworkResult<List<org.example.project.data.model.PreTaskData>> = safeApiCall {
        httpClient.post(ApiEndpoints.PRE_TASK_LIST) {
            jsonBody(request)
        }
    }

    override suspend fun getPreTaskContent(
        request: org.example.project.data.model.PreTaskContentRequest
    ): NetworkResult<org.example.project.data.model.PreTaskContentResponseData> {
        return safeApiCall {
            httpClient.post(ApiEndpoints.PRE_TASK_CONTENT) {
                jsonBody(request)
            }
        }
    }

    override suspend fun createPreTask(
        request: org.example.project.data.model.CreatePreTaskRequest
    ): NetworkResult<org.example.project.data.model.CreatePreTaskResponseData> {
        return safeApiCall {
            httpClient.post(ApiEndpoints.PRE_TASK_CREATE) {
                jsonBody(request)
            }
        }
    }

    override suspend fun generateInspectionExcel(
        request: org.example.project.data.model.InspectionListRequest
    ): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.GENERATE_INSPECTION_EXCEL) {
            jsonBody(request)
        }
    }

    override suspend fun generateIncidentExcel(
        request: org.example.project.data.model.IncidentRequest
    ): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.GENERATE_INCIDENT_EXCEL) {
            jsonBody(request)
        }
    }

    override suspend fun generateObservationExcel(
        request: org.example.project.data.model.ObservationRequest
    ): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.GENERATE_OBSERVATION_EXCEL) {
            jsonBody(request)
        }
    }

    override suspend fun generateViolationExcel(
        request: org.example.project.data.model.ViolationListRequest
    ): NetworkResult<CommonModelResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.GENERATE_VIOLATION_EXCEL) {
            jsonBody(request)
        }
    }

    override suspend fun createViolation(
        request: org.example.project.data.model.CreateViolationRequest
    ): NetworkResult<org.example.project.data.model.CreateViolationResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.CREATE_VIOLATION) {
            jsonBody(request)
        }
    }

    override suspend fun getAuditItems(): NetworkResult<org.example.project.data.model.AuditItemsResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.AUDIT_ITEMS) {
            jsonBody(emptyMap<String, String>()) // Assuming no body params as per spec
        }
    }

    override suspend fun getStaticEquipmentsList(
        request: org.example.project.data.model.StaticEquipmentListRequest
    ): NetworkResult<org.example.project.data.model.StaticEquipmentListResponse> {
        return safeApiCall {
            httpClient.post(ApiEndpoints.STATIC_EQUIPMENTS_LIST) {
                jsonBody(request)
            }
        }
    }

    override suspend fun addInspection(
        request: org.example.project.data.model.AddInspectionRequest
    ): NetworkResult<org.example.project.data.model.AddInspectionResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.ADD_INSPECTION) {
            jsonBody(request)
        }
    }

    override suspend fun getInspectionDetail(
        request: org.example.project.data.model.InspectionDetailRequest
    ): NetworkResult<org.example.project.data.model.InspectionDetailResponse> = safeApiCall {
        httpClient.post(ApiEndpoints.INSPECTION_DETAILS) {
            jsonBody(request)
        }
    }

    override suspend fun getPreTaskDetail(
        request: org.example.project.data.model.PreTaskDetailRequest
    ): NetworkResult<org.example.project.data.model.PreTaskDetailResponseData> = safeApiCall {
        httpClient.post(ApiEndpoints.PRE_TASK_DETAIL) {
            jsonBody(request)
        }
    }
    
    override suspend fun createLessonLearned(
        request: org.example.project.data.model.CreateLessonLearnedRequest
    ): NetworkResult<org.example.project.data.model.CreateLessonLearnedResponseData> = safeApiCall {
        httpClient.post(ApiEndpoints.LESSON_LEARNED_CREATE) {
            jsonBody(request)
        }
    }
    
    override suspend fun getLessonsLearnedList(
        request: org.example.project.data.model.LessonLearnedListRequest
    ): NetworkResult<List<org.example.project.data.model.LessonLearnedData>> = safeApiCall {
        httpClient.post(ApiEndpoints.LESSON_LEARNED_LIST) {
            jsonBody(request)
        }
    }
    
    override suspend fun getLessonLearnedDetail(
        request: org.example.project.data.model.LessonLearnedDetailRequest
    ): NetworkResult<org.example.project.data.model.LessonLearnedDetailResponseData> = safeApiCall {
        httpClient.post(ApiEndpoints.LESSON_LEARNED_DETAIL) {
            jsonBody(request)
        }
    }
}
