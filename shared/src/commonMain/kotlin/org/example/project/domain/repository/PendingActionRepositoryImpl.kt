package org.example.project.domain.repository

import org.example.project.data.model.PendingActionData
import org.example.project.data.model.PendingActionRequest
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult

class PendingActionRepositoryImpl(
    private val apiService: AuthApiService
) : PendingActionRepository {
    override suspend fun getPendingActions(): NetworkResult<PendingActionData> {
        return apiService.getPendingActions(PendingActionRequest())
    }
}
