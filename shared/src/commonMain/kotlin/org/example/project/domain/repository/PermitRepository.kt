package org.example.project.domain.repository

import org.example.project.data.model.PermitListRequest
import org.example.project.data.model.PermitListResponse
import org.example.project.network.NetworkResult
import org.example.project.data.model.FilterContentData

interface PermitRepository {
    suspend fun getPermitList(request: PermitListRequest): NetworkResult<PermitListResponse>
    suspend fun getFilterContent(): NetworkResult<FilterContentData>
    suspend fun getPermitTypes(): NetworkResult<org.example.project.data.model.PermitTypesResponse>
    suspend fun getPermitPendingActions(): NetworkResult<org.example.project.data.model.PermitPendingActionsResponse>
}
