package org.example.project.domain.repository

import org.example.project.data.model.FilterContentData
import org.example.project.data.model.IncidentData
import org.example.project.data.model.IncidentRequest
import org.example.project.network.NetworkResult

interface IncidentRepository {
    suspend fun getIncidentList(request: IncidentRequest): NetworkResult<List<IncidentData>>
    suspend fun getFilterContent(): NetworkResult<FilterContentData>
    suspend fun addIncident(request: org.example.project.data.model.AddIncidentRequest): NetworkResult<org.example.project.data.model.AddIncidentData>
    suspend fun getIncidentDetail(request: org.example.project.data.model.IncidentDetailRequest): NetworkResult<org.example.project.data.model.IncidentDetailResponse>

    suspend fun generateIncidentExcel(
        request: org.example.project.data.model.IncidentRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse>

    suspend fun generatePdf(
        request: org.example.project.data.model.GenerateIncidentPdfRequest
    ): NetworkResult<org.example.project.data.model.CommonModelResponse>
}
