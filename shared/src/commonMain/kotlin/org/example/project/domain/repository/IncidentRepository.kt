package org.example.project.domain.repository

import org.example.project.data.model.FilterContentData
import org.example.project.data.model.IncidentData
import org.example.project.data.model.IncidentRequest
import org.example.project.network.NetworkResult

interface IncidentRepository {
    suspend fun getIncidentList(request: IncidentRequest): NetworkResult<List<IncidentData>>
    suspend fun getFilterContent(): NetworkResult<FilterContentData>
    suspend fun addIncident(request: org.example.project.data.model.AddIncidentRequest): NetworkResult<org.example.project.data.model.AddIncidentResponse>
}
