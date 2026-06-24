package org.example.project.domain.repository

import org.example.project.data.model.PendingActionData
import org.example.project.network.NetworkResult

interface PendingActionRepository {
    suspend fun getPendingActions(): NetworkResult<PendingActionData>
}
