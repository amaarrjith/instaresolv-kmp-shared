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

    override suspend fun addIncident(request: org.example.project.data.model.AddIncidentRequest): NetworkResult<org.example.project.data.model.AddIncidentResponse> {
        return apiService.addIncident(request)
    }
}
