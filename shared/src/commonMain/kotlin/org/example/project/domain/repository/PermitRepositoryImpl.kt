package org.example.project.domain.repository

import org.example.project.data.model.PermitListRequest
import org.example.project.data.model.PermitListResponse
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult
import org.example.project.data.model.FilterContentData

class PermitRepositoryImpl(
    private val apiService: AuthApiService
) : PermitRepository {
    override suspend fun getPermitList(request: PermitListRequest): NetworkResult<PermitListResponse> {
        return apiService.getPermitList(request)
    }

    override suspend fun getFilterContent(): NetworkResult<FilterContentData> {
        return apiService.getFilterContent()
    }

    override suspend fun getPermitTypes(): NetworkResult<org.example.project.data.model.PermitTypesResponse> {
        return apiService.getPermitTypes()
    }

    override suspend fun getPermitPendingActions(): NetworkResult<org.example.project.data.model.PermitPendingActionsResponse> {
        return apiService.getPermitPendingActions(org.example.project.data.model.PermitPendingActionsRequest())
    }

    override suspend fun getPermitContents(request: org.example.project.data.model.PermitContentRequest): NetworkResult<org.example.project.data.model.PermitContentData> {
        return apiService.getPermitContents(request)
    }

    override suspend fun getPermitProjectList(request: org.example.project.data.model.PermitProjectListRequest): NetworkResult<org.example.project.data.model.PermitProjectListResponse> {
        return apiService.getPermitProjectList(request)
    }

    override suspend fun getPermitUserList(request: org.example.project.data.model.PermitUserListRequest): NetworkResult<org.example.project.data.model.PermitUserListResponse> {
        return apiService.getPermitUserList(request)
    }

    override suspend fun submitPermitValidity(request: org.example.project.data.model.PermitValiditySubmitRequest): NetworkResult<org.example.project.data.model.PermitValiditySubmitResponse> {
        return apiService.submitPermitValidity(request)
    }

    override suspend fun getPermitDetail(request: org.example.project.data.model.PermitDetailRequest): NetworkResult<org.example.project.data.model.PermitDetailData> {
        return apiService.getPermitDetail(request)
    }

    override suspend fun submitPermitAuthorization(request: org.example.project.data.model.PermitAuthorizationSubmitRequest): NetworkResult<org.example.project.data.model.PermitAuthorizationSubmitResponse> {
        return apiService.submitPermitAuthorization(request)
    }

    override suspend fun submitPermitAction(request: org.example.project.data.model.PermitActionRequest): NetworkResult<org.example.project.data.model.CommonModelResponse> {
        return apiService.submitPermitAction(request)
    }

    override suspend fun submitPermitClosureRequest(request: org.example.project.data.model.PermitClosureSubmitRequest): NetworkResult<org.example.project.data.model.CommonModelResponse> {
        return apiService.submitPermitClosureRequest(request)
    }

    override suspend fun submitPermitCertificateClosure(request: org.example.project.data.model.PermitCertificateClosureSubmitRequest): NetworkResult<org.example.project.data.model.CommonModelResponse> {
        return apiService.submitPermitCertificateClosure(request)
    }

    override suspend fun generatePermitExcel(request: org.example.project.data.model.PermitExcelRequest): NetworkResult<org.example.project.data.model.CommonModelResponse> {
        return apiService.generatePermitExcel(request)
    }
}
