package org.example.project.domain.repository

import org.example.project.data.model.ViolationData
import org.example.project.data.model.ViolationListRequest
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult

class ViolationRepositoryImpl(
    private val apiService: AuthApiService
) : ViolationRepository {
    override suspend fun getViolationList(request: ViolationListRequest): NetworkResult<List<ViolationData>> {
        return apiService.getViolationList(request)
    }

    override suspend fun getViolationDetail(request: org.example.project.data.model.ViolationDetailRequest): NetworkResult<ViolationData> {
        return apiService.getViolationDetail(request)
    }
}
