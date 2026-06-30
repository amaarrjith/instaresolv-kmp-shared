package org.example.project.domain.repository

import org.example.project.data.model.InspectionData
import org.example.project.data.model.InspectionListRequest
import org.example.project.network.NetworkResult

interface InspectionRepository {
    suspend fun getInspectionList(request: InspectionListRequest): NetworkResult<List<InspectionData>>
    suspend fun getAuditItems(): NetworkResult<org.example.project.data.model.AuditItemsResponse>

    suspend fun getStaticEquipmentsList(
        request: org.example.project.data.model.StaticEquipmentListRequest
    ): NetworkResult<org.example.project.data.model.StaticEquipmentListResponse>

    suspend fun addInspection(
        request: org.example.project.data.model.AddInspectionRequest
    ): NetworkResult<org.example.project.data.model.AddInspectionResponse>

    suspend fun getInspectionDetail(
        request: org.example.project.data.model.InspectionDetailRequest
    ): NetworkResult<org.example.project.data.model.InspectionDetailResponse>

    suspend fun generateInspectionExcel(
        request: org.example.project.data.model.InspectionListRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse>
}
