package org.example.project.network

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    fun currentlyConnected(): Boolean

    val isNetworkConnected: Flow<Boolean>
}