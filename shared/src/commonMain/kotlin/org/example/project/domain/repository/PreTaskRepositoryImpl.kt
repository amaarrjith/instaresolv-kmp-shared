package org.example.project.domain.repository

import org.example.project.data.model.PreTaskContentRequest
import org.example.project.data.model.PreTaskContentResponseData
import org.example.project.data.model.PreTaskData
import org.example.project.data.model.PreTaskListRequest
import org.example.project.data.model.CreatePreTaskRequest
import org.example.project.data.model.CreatePreTaskResponseData
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult

class PreTaskRepositoryImpl(
    private val apiService: AuthApiService
) : PreTaskRepository {
    override suspend fun getPreTaskList(request: PreTaskListRequest): NetworkResult<List<PreTaskData>> {
        return apiService.getPreTaskList(request)
    }

    override suspend fun getPreTaskContent(request: PreTaskContentRequest): NetworkResult<PreTaskContentResponseData> {
        return apiService.getPreTaskContent(request)
    }

    override suspend fun createPreTask(request: CreatePreTaskRequest): NetworkResult<CreatePreTaskResponseData> {
        return apiService.createPreTask(request)
    }

    override suspend fun getPreTaskDetail(request: org.example.project.data.model.PreTaskDetailRequest): NetworkResult<org.example.project.data.model.PreTaskDetailResponseData> {
        return apiService.getPreTaskDetail(request)
    }
}
