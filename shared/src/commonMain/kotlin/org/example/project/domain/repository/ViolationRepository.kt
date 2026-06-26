package org.example.project.domain.repository

import org.example.project.data.model.ViolationData
import org.example.project.data.model.ViolationListRequest
import org.example.project.network.NetworkResult

interface ViolationRepository {
    suspend fun getViolationList(request: ViolationListRequest): NetworkResult<List<ViolationData>>
    
    suspend fun getViolationDetail(request: org.example.project.data.model.ViolationDetailRequest): NetworkResult<ViolationData>
}
