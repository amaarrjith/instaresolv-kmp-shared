package org.example.project.domain.repository

import org.example.project.data.model.ToolBoxTalkListRequest
import org.example.project.data.model.ToolBoxTalkExcelRequest
import org.example.project.data.model.ToolBoxTalkItem
import org.example.project.data.model.CommonModelResponse
import org.example.project.network.NetworkResult

interface ToolBoxTalkRepository {
    suspend fun getToolBoxTalkList(request: ToolBoxTalkListRequest): NetworkResult<List<ToolBoxTalkItem>>
    suspend fun generateToolBoxTalkExcel(request: ToolBoxTalkExcelRequest): NetworkResult<CommonModelResponse>
    suspend fun createToolBoxTalk(request: org.example.project.data.model.CreateToolBoxTalkRequest): NetworkResult<org.example.project.data.model.CreateToolBoxTalkResponseData>
    suspend fun getToolBoxTalkDetail(request: org.example.project.data.model.ToolBoxTalkDetailRequest): NetworkResult<org.example.project.data.model.ToolBoxTalkItem>
}
