package org.example.project.domain.repository

import org.example.project.data.model.ApproveRejectRequest
import org.example.project.data.model.ApproveRejectResponse
import org.example.project.data.model.CommonModelResponse
import org.example.project.data.model.DeleteObservationRequest
import org.example.project.data.model.RequestDeleteObservationRequest
import org.example.project.data.model.RequestResponsiblePersonChangeRequest
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

    override suspend fun closeObservation(
        request: org.example.project.data.model.CloseObservationRequest
    ): NetworkResult<kotlinx.serialization.json.JsonObject> {
        return apiService.closeObservation(request)
    }

    override suspend fun generateObservationExcel(
        request: ObservationRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse> {
        return apiService.generateObservationExcel(request)
    }

    override suspend fun generatePdf(
        request: org.example.project.data.model.GenerateObservationPdfRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse> {
        return apiService.generateObservationPdf(request)
    }

    override suspend fun deleteObservation(request: DeleteObservationRequest): NetworkResult<CommonModelResponse> {
        return apiService.deleteObservation(request)
    }

    override suspend fun approveOrReject(request: ApproveRejectRequest): NetworkResult<ApproveRejectResponse> {
        return apiService.approveOrReject(request)
    }

    override suspend fun requestToDeleteObservation(request: RequestDeleteObservationRequest): NetworkResult<CommonModelResponse> {
        return apiService.requestToDeleteObservation(request)
    }

    override suspend fun requestResponsiblePersonChange(request: RequestResponsiblePersonChangeRequest): NetworkResult<CommonModelResponse> {
        return apiService.requestResponsiblePersonChange(request)
    }
}
