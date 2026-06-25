package org.example.project.domain.repository

import org.example.project.data.model.ObservationData
import org.example.project.data.model.ObservationRequest
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult
import org.example.project.data.model.FilterContentData

class ObservationRepositoryImpl(
    private val apiService: AuthApiService
) : ObservationRepository {
    override suspend fun getObservationList(request: ObservationRequest): NetworkResult<ObservationData> {
        return apiService.getObservationList(request)
    }

    override suspend fun getFilterContent(): NetworkResult<FilterContentData> {
        return apiService.getFilterContent()
    }

    override suspend fun createObservation(
        request: org.example.project.data.model.CreateObservationRequest
    ): NetworkResult<org.example.project.data.model.CommonResponse<org.example.project.data.model.CreateObservationResponse>> {
        return apiService.createObservation(request)
    }

    override suspend fun getObservationDetail(
        request: org.example.project.data.model.ObservationDetailRequest
    ): NetworkResult<org.example.project.data.model.ObservationDetailResponse> {
        return apiService.getObservationDetail(request)
    }
}
