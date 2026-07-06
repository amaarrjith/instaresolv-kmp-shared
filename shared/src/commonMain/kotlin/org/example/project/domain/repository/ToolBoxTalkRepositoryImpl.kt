package org.example.project.domain.repository

import org.example.project.data.model.ToolBoxTalkListRequest
import org.example.project.data.model.ToolBoxTalkExcelRequest
import org.example.project.data.model.ToolBoxTalkItem
import org.example.project.data.model.CommonModelResponse
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult

class ToolBoxTalkRepositoryImpl(
    private val apiService: AuthApiService
) : ToolBoxTalkRepository {
    override suspend fun getToolBoxTalkList(request: ToolBoxTalkListRequest): NetworkResult<List<ToolBoxTalkItem>> {
        return apiService.getToolBoxTalkList(request)
    }

    override suspend fun generateToolBoxTalkExcel(request: ToolBoxTalkExcelRequest): NetworkResult<CommonModelResponse> {
        return apiService.generateToolBoxTalkExcel(request)
    }

    override suspend fun createToolBoxTalk(request: org.example.project.data.model.CreateToolBoxTalkRequest): NetworkResult<org.example.project.data.model.CreateToolBoxTalkResponseData> {
        return apiService.createToolBoxTalk(request)
    }

    override suspend fun getToolBoxTalkDetail(request: org.example.project.data.model.ToolBoxTalkDetailRequest): NetworkResult<org.example.project.data.model.ToolBoxTalkItem> {
        return apiService.getToolBoxTalkDetail(request)
    }
}
