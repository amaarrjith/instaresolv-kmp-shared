package org.example.project.domain.repository

import org.example.project.data.model.ViolationData
import org.example.project.data.model.ViolationListRequest
import org.example.project.network.NetworkResult

interface ViolationRepository {
    suspend fun getViolationList(request: ViolationListRequest): NetworkResult<List<ViolationData>>
    
    suspend fun getViolationDetail(request: org.example.project.data.model.ViolationDetailRequest): NetworkResult<ViolationData>
    
    suspend fun generateViolationExcel(request: ViolationListRequest): NetworkResult<org.example.project.data.model.CommonModelResponse>

    suspend fun generatePdf(
        request: org.example.project.data.model.GenerateViolationPdfRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse>

    suspend fun createViolation(
        request: org.example.project.data.model.CreateViolationRequest
    ): NetworkResult<org.example.project.data.model.CreateViolationResponse>
}
