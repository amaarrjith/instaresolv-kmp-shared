package org.example.project.domain.repository

import org.example.project.data.model.FilterContentData
import org.example.project.data.model.IncidentData
import org.example.project.data.model.IncidentRequest
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult

class IncidentRepositoryImpl(
    private val apiService: AuthApiService
) : IncidentRepository {
    override suspend fun getIncidentList(request: IncidentRequest): NetworkResult<List<IncidentData>> {
        return apiService.getIncidentList(request)
    }

    override suspend fun getFilterContent(): NetworkResult<FilterContentData> {
        return apiService.getFilterContent()
    }

    override suspend fun addIncident(request: org.example.project.data.model.AddIncidentRequest): NetworkResult<org.example.project.data.model.AddIncidentData> {
        return apiService.addIncident(request)
    }

    override suspend fun getIncidentDetail(request: org.example.project.data.model.IncidentDetailRequest): NetworkResult<org.example.project.data.model.IncidentDetailResponse> {
        return apiService.getIncidentDetail(request)
    }

    override suspend fun generateIncidentExcel(request: org.example.project.data.model.IncidentRequest): NetworkResult<org.example.project.data.model.CommonModelResponse> {
        return apiService.generateIncidentExcel(request)
    }

    override suspend fun generatePdf(request: org.example.project.data.model.GenerateIncidentPdfRequest): NetworkResult<org.example.project.data.model.CommonModelResponse> {
        return apiService.generateIncidentPdf(request)
    }
}
