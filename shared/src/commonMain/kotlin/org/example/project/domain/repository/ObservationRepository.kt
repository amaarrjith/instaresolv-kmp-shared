package org.example.project.domain.repository

import org.example.project.data.model.ObservationData
import org.example.project.data.model.ObservationRequest
import org.example.project.network.NetworkResult
import org.example.project.data.model.FilterContentData

interface ObservationRepository {
    suspend fun getObservationList(request: ObservationRequest): NetworkResult<ObservationData>
    suspend fun getFilterContent(): NetworkResult<FilterContentData>
    suspend fun createObservation(
        request: org.example.project.data.model.CreateObservationRequest
    ): NetworkResult<org.example.project.data.model.CommonResponse<org.example.project.data.model.CreateObservationResponse>>
    
    suspend fun getObservationDetail(
        request: org.example.project.data.model.ObservationDetailRequest
    ): NetworkResult<org.example.project.data.model.ObservationDetailResponse>
}
