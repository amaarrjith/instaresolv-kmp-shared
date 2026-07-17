package org.example.project.domain.repository

import org.example.project.data.model.CommonModelResponse
import org.example.project.data.model.PermitListRequest
import org.example.project.data.model.PermitListResponse
import org.example.project.network.NetworkResult
import org.example.project.data.model.FilterContentData

interface PermitRepository {
    suspend fun getPermitList(request: PermitListRequest): NetworkResult<PermitListResponse>
    suspend fun getFilterContent(): NetworkResult<FilterContentData>
    suspend fun getPermitTypes(): NetworkResult<org.example.project.data.model.PermitTypesResponse>
    suspend fun getPermitPendingActions(): NetworkResult<org.example.project.data.model.PermitPendingActionsResponse>
    suspend fun getPermitContents(request: org.example.project.data.model.PermitContentRequest): NetworkResult<org.example.project.data.model.PermitContentData>
    suspend fun getPermitProjectList(request: org.example.project.data.model.PermitProjectListRequest): NetworkResult<org.example.project.data.model.PermitProjectListResponse>
    suspend fun getPermitUserList(request: org.example.project.data.model.PermitUserListRequest): NetworkResult<org.example.project.data.model.PermitUserListResponse>
    suspend fun submitPermitValidity(request: org.example.project.data.model.PermitValiditySubmitRequest): NetworkResult<org.example.project.data.model.PermitValiditySubmitResponse>
    suspend fun getPermitDetail(request: org.example.project.data.model.PermitDetailRequest): NetworkResult<org.example.project.data.model.PermitDetailData>
    suspend fun submitPermitAuthorization(request: org.example.project.data.model.PermitAuthorizationSubmitRequest): NetworkResult<org.example.project.data.model.PermitAuthorizationSubmitResponse>
    suspend fun submitPermitAction(request: org.example.project.data.model.PermitActionRequest): NetworkResult<CommonModelResponse>
    suspend fun submitPermitClosureRequest(request: org.example.project.data.model.PermitClosureSubmitRequest): NetworkResult<org.example.project.data.model.CommonModelResponse>
    suspend fun submitPermitCertificateClosure(request: org.example.project.data.model.PermitCertificateClosureSubmitRequest): NetworkResult<org.example.project.data.model.CommonModelResponse>
}
