package org.example.project.domain.repository

import org.example.project.data.model.ForgetPasswordResponse
import org.example.project.data.model.HomeResponse
import org.example.project.data.model.ImageUploadData
import org.example.project.data.model.LoginResponse
import org.example.project.data.model.NotificationListResponse
import org.example.project.data.model.OTPResponse
import org.example.project.data.model.ProjectAccessRequest
import org.example.project.data.model.ProjectAccessResponse
import org.example.project.data.model.RegisterResponse
import org.example.project.data.model.UserCheckoutRequest
import org.example.project.data.model.UserEditResponse
import org.example.project.data.model.UserResponse
import org.example.project.network.NetworkResult

interface AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): NetworkResult<LoginResponse>

    suspend fun forgetPassword(
        email: String
    ): NetworkResult<ForgetPasswordResponse>

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        designation: String,
        company: String,
        isTermsAccepted: Boolean
    ): NetworkResult<RegisterResponse>

    suspend fun verifyOTP(
        tempUserId: Int,
        email: String,
        otp: String
    ): NetworkResult<OTPResponse>

    suspend fun userCheckOut(
        uuid: String
    ): NetworkResult<UserResponse>

    suspend fun requestProjectAccess(
        request: ProjectAccessRequest
    ): NetworkResult<ProjectAccessResponse>

    suspend fun uploadImage(
        imageBytes: ByteArray,
        fileName: String,
        type: Int
    ): NetworkResult<ImageUploadData>

    suspend fun getHomeContents(
        userId: Int
    ): NetworkResult<HomeResponse>

    suspend fun getNotificationList() : NetworkResult<NotificationListResponse>
    suspend fun editProfile(
        name: String,
        profileImage: String,
        company: String,
        designation: String
    ): NetworkResult<UserEditResponse>

    suspend fun getGeneralContents(
        type: Int
    ): NetworkResult<org.example.project.data.model.GeneralContentsResponse>

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): NetworkResult<org.example.project.data.model.CommonModelResponse>

    suspend fun getDeleteAccountTerms(
        request: org.example.project.data.model.DeleteAccountTermsRequest
    ): NetworkResult<org.example.project.data.model.DeleteAccountTermsData>

    suspend fun requestDeleteAccount(
        request: org.example.project.data.model.DeleteAccountRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse>

    suspend fun verifyDeleteAccount(
        request: org.example.project.data.model.DeleteAccountRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse>

    suspend fun sendContactMessage(
        request: org.example.project.data.model.ContactMessageRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse>
}
