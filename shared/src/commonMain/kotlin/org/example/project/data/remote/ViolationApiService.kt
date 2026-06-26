package org.example.project.data.remote

import org.example.project.data.model.ViolationData
import org.example.project.data.model.ViolationListRequest
import org.example.project.network.NetworkResult

interface ViolationApiService {
    suspend fun getViolationList(request: ViolationListRequest): NetworkResult<List<ViolationData>>
}
