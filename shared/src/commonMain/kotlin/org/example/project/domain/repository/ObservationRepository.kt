package org.example.project.domain.repository

import org.example.project.data.model.ApproveRejectRequest
import org.example.project.data.model.ApproveRejectResponse
import org.example.project.data.model.CommonModelResponse
import org.example.project.data.model.DeleteObservationRequest
import org.example.project.data.model.RequestDeleteObservationRequest
import org.example.project.data.model.RequestResponsiblePersonChangeRequest
import org.example.project.data.model.ObservationData
import org.example.project.data.model.ObservationRequest
import org.example.project.network.NetworkResult
import org.example.project.data.model.FilterContentData

interface ObservationRepository {
    suspend fun getObservationList(request: ObservationRequest): NetworkResult<ObservationData>

    suspend fun createObservation(
        request: org.example.project.data.model.CreateObservationRequest
    ): NetworkResult<org.example.project.data.model.CommonResponse<org.example.project.data.model.CreateObservationResponse>>
    
    suspend fun getObservationDetail(
        request: org.example.project.data.model.ObservationDetailRequest
    ): NetworkResult<org.example.project.data.model.ObservationDetailResponse>

    suspend fun closeObservation(
        request: org.example.project.data.model.CloseObservationRequest
    ): NetworkResult<kotlinx.serialization.json.JsonObject>

    suspend fun generateObservationExcel(
        request: ObservationRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse>

    suspend fun generatePdf(
        request: org.example.project.data.model.GenerateObservationPdfRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse>

    suspend fun deleteObservation(
        request: DeleteObservationRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun approveOrReject(
        request: ApproveRejectRequest
    ): NetworkResult<ApproveRejectResponse>

    suspend fun requestToDeleteObservation(
        request: RequestDeleteObservationRequest
    ): NetworkResult<CommonModelResponse>

    suspend fun requestResponsiblePersonChange(
        request: RequestResponsiblePersonChangeRequest
    ): NetworkResult<CommonModelResponse>
}
