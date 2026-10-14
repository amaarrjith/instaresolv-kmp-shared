package org.example.project.domain.repository

import org.example.project.data.model.PreTaskContentRequest
import org.example.project.data.model.PreTaskContentResponseData
import org.example.project.data.model.PreTaskData
import org.example.project.data.model.PreTaskListRequest
import org.example.project.data.model.CreatePreTaskRequest
import org.example.project.data.model.CreatePreTaskResponseData
import org.example.project.network.NetworkResult

interface PreTaskRepository {
    suspend fun getPreTaskList(request: PreTaskListRequest): NetworkResult<List<PreTaskData>>
    suspend fun getPreTaskContent(request: PreTaskContentRequest): NetworkResult<PreTaskContentResponseData>
    suspend fun createPreTask(request: CreatePreTaskRequest): NetworkResult<CreatePreTaskResponseData>
    suspend fun getPreTaskDetail(request: org.example.project.data.model.PreTaskDetailRequest): NetworkResult<org.example.project.data.model.PreTaskDetailResponseData>
    suspend fun generatePreTaskExcel(request: PreTaskListRequest): NetworkResult<org.example.project.data.model.CommonModelResponse>
    suspend fun generatePreTaskPdf(request: org.example.project.data.model.GeneratePreTaskPdfRequest): NetworkResult<org.example.project.data.model.CommonModelResponse>
}
