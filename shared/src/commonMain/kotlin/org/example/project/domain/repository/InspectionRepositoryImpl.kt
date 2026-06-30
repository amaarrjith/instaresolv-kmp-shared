package org.example.project.domain.repository

import org.example.project.data.model.InspectionData
import org.example.project.data.model.InspectionListRequest
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult

class InspectionRepositoryImpl(
    private val apiService: AuthApiService
) : InspectionRepository {
    override suspend fun getInspectionList(request: InspectionListRequest): NetworkResult<List<InspectionData>> {
        return apiService.getInspectionList(request)
    }

    override suspend fun getAuditItems(): NetworkResult<org.example.project.data.model.AuditItemsResponse> {
        return apiService.getAuditItems()
    }

    override suspend fun getStaticEquipmentsList(
        request: org.example.project.data.model.StaticEquipmentListRequest
    ): NetworkResult<org.example.project.data.model.StaticEquipmentListResponse> {
        return apiService.getStaticEquipmentsList(request)
    }

    override suspend fun addInspection(
        request: org.example.project.data.model.AddInspectionRequest
    ): NetworkResult<org.example.project.data.model.AddInspectionResponse> {
        return apiService.addInspection(request)
    }

    override suspend fun getInspectionDetail(
        request: org.example.project.data.model.InspectionDetailRequest
    ): NetworkResult<org.example.project.data.model.InspectionDetailResponse> {
        return apiService.getInspectionDetail(request)
    }

    override suspend fun generateInspectionExcel(
        request: org.example.project.data.model.InspectionListRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse> {
        return apiService.generateInspectionExcel(request)
    }
}
