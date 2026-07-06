package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ToolBoxTalkDetailRequest(
    val id: Int
)

@Serializable
data class ToolBoxTalkListRequest(
    val searchKey: String? = null,
    val pageNumber: Int,
    val limit: Int,
    val sortType: Int,
    val projectIds: List<Int>? = null,
    val openDate: String? = null,
    val endDate: String? = null,
    val reportedByPersons: List<Int>? = null
)

@Serializable
data class ToolBoxTalkExcelRequest(
    val searchKey: String? = null,
    val sortBy: Int,
    val projectIds: List<Int>? = null,
    val openDate: String? = null,
    val endDate: String? = null,
    val reportedByPersons: List<Int>? = null
)

@Serializable
data class ToolBoxTalkItem(
    val id: Int,
    val reportedBy: String? = null,
    val date: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val topic: String? = null,
    val discussionPoints: List<DiscussionPoint>? = null,
    val attendees: List<Attendee>? = null,
    val createdAt: String? = null,
    val facilities: ToolBoxTalkFacilityData? = null,
    val images: List<ToolBoxTalkImageData>? = null
)

@Serializable
data class DiscussionPoint(
    val id: Int,
    val point: String? = null
)

@Serializable
data class Attendee(
    val id: String? = null,
    val employeeCode: String? = null,
    val employeeName: String? = null,
    val companyName: String? = null,
    val profession: String? = null
)

@Serializable
data class ToolBoxTalkFacilityData(
    val groupId: String? = null,
    val groupName: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null
)

@Serializable
data class ToolBoxTalkImageData(
    val image: String? = null,
    val description: String? = null
)
