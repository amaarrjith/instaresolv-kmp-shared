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
}
