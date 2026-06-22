package org.example.project.data.repository

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
import org.example.project.data.model.RegisterRequest
import org.example.project.data.model.RegisterResponse
import org.example.project.data.model.UserCheckoutRequest
import org.example.project.data.model.UserEditRequest
import org.example.project.data.model.UserEditResponse
import org.example.project.data.model.UserResponse
import org.example.project.data.remote.api.AuthApiService
import org.example.project.domain.repository.AuthRepository
import org.example.project.network.NetworkResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AuthRepositoryImpl(
    private val apiService: AuthApiService
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): NetworkResult<LoginResponse> {
        return apiService.login(
            LoginRequest(
                email = email,
                password = password
            )
        )
    }

    override suspend fun forgetPassword(
        email: String
    ): NetworkResult<ForgetPasswordResponse> {
        return apiService.forgetPassword(
            ForgetPasswordRequest(
                email = email
            )
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        designation: String,
        company: String,
        isTermsAccepted: Boolean
    ): NetworkResult<RegisterResponse> {
        return apiService.register(
            RegisterRequest(
                uuid = Uuid.random().toString(),
                name = fullName,
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                designation = designation,
                company = company,
                profileImage = ""
            )
        )
    }

    override suspend fun verifyOTP(
        tempUserId: Int,
        email: String,
        otp: String
    ): NetworkResult<OTPResponse> {
        return apiService.verifyOTP(
            OTPRequest(
                tempUserId = tempUserId,
                email = email,
                otp = otp
            )
        )
    }

    override suspend fun userCheckOut(uuid: String): NetworkResult<UserResponse> {
      return apiService.userCheckOut(
          UserCheckoutRequest(
          uuid = uuid
        )
      )
    }

    override suspend fun requestProjectAccess(request: ProjectAccessRequest): NetworkResult<ProjectAccessResponse> {
        return apiService.requestProjectAccess(request)
    }

    override suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        type: Int
    ): NetworkResult<ImageUploadData> {
        return apiService.uploadImage(imageBytes, fileName, type)
    }

    override suspend fun getHomeContents(userId: Int): NetworkResult<HomeResponse> {
        return apiService.getHomeContents(
            request = HomeContentsRequest(
                userId = userId
            )
        )
    }

    override suspend fun getNotificationList(): NetworkResult<NotificationListResponse> {
        return apiService.getNotificationList()
    }

    override suspend fun editProfile(
        name: String,
        profileImage: String,
        company: String,
        designation: String
    ): NetworkResult<UserEditResponse> {
        return apiService.userEdit(
            UserEditRequest(
                name = name,
                profileImage = profileImage,
                company = company,
                designation = designation
            )
        )
    }
}
