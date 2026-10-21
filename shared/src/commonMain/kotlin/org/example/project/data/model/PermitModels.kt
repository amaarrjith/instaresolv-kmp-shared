package org.example.project.data.model

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import instaresolv.shared.generated.resources.*
import org.example.project.config.BrandStrings

@Serializable
enum class PermitStatus(val value: Int, val title: String, val colorHex: Long) {
    OPEN(2, "Open", 0xFF00A82B),
    CANCELLED(3, "Cancelled", 0xFFD42027),
    SUSPENDED(4, "Suspended", 0xFFE5A93C),
    EXPIRED(5, "Expired", 0xFF757575),
    CLOSED(6, "Closed", 0xFF2E6AC6),
    PENDING_ALNASR_AUTHORIZATION(8, "Pending IR Authorization", 0xFFE5A93C),
    PENDING_ALNASR_CLOSURE(9, "Pending IR Closure", 0xFFE5A93C),
    PENDING_SUBCONTRACTOR_AUTHORIZATION(10, "Pending SC Authorization", 0xFFE5A93C),
    PENDING_SUBCONTRACTOR_CLOSURE(11, "Pending SC Closure", 0xFFE5A93C),
    PENDING_ALNASR_AUTHORIZATION_AND_EXPIRED(12, "Pending IR Authorization & Expired", 0xFF757575),
    PENDING_ALNASR_CLOSURE_AND_EXPIRED(13, "Pending IR Closure & Expired", 0xFF757575),
    PENDING_SUBCONTRACTOR_AUTHORIZATION_AND_EXPIRED(14, "Pending SC Authorization & Expired", 0xFF757575),
    PENDING_SUBCONTRACTOR_CLOSURE_AND_EXPIRED(15, "Pending SC Closure & Expired", 0xFF757575);

    val stringRes: StringResource
        get() = when (this) {
            OPEN -> Res.string.open
            CANCELLED -> Res.string.cancelled
            SUSPENDED -> Res.string.suspended
            EXPIRED -> Res.string.expired
            CLOSED -> Res.string.closed
            PENDING_ALNASR_AUTHORIZATION -> BrandStrings.pendingAuthShort
            PENDING_ALNASR_CLOSURE -> BrandStrings.pendingClosureShort
            PENDING_SUBCONTRACTOR_AUTHORIZATION -> Res.string.pending_sc_authorization
            PENDING_SUBCONTRACTOR_CLOSURE -> Res.string.pending_sc_closure
            PENDING_ALNASR_AUTHORIZATION_AND_EXPIRED -> BrandStrings.pendingAuthExpiredShort
            PENDING_ALNASR_CLOSURE_AND_EXPIRED -> BrandStrings.pendingClosureExpiredShort
            PENDING_SUBCONTRACTOR_AUTHORIZATION_AND_EXPIRED -> Res.string.pending_sc_authorization_and_expired
            PENDING_SUBCONTRACTOR_CLOSURE_AND_EXPIRED -> Res.string.pending_sc_closure_and_expired
        }

    companion object {
        fun fromValue(value: Int): PermitStatus? {
            return entries.find { it.value == value }
        }
    }
}

@Serializable
data class PermitListRequest(
    val searchKey: String = "",
    val pageNumber: Int = 1,
    val limit: Int = 10,
    val sortType: Int = 1,
    val projectIds: List<Int>? = null,
    val authorizer: List<Int>? = null,
    val requestor: List<Int>? = null,
    val hseAssigned: List<Int>? = null,
    val status: List<Int>? = null,
    val permitTypes: List<Int>? = null,
    val openDate: String? = null,
    val closeDate: String? = null,
    val validity: Int? = null
)

@Serializable
data class PermitType(
    val id: Int,
    val title: String
)

@Serializable
data class PermitFacility(
    val groupId: String? = null,
    val groupName: String? = null,
    val groupCode: String? = null,
    val groupImage: String? = null
)

@Serializable
data class PermitItem(
    val id: Int,
    val permitCode: String? = null,
    val createdAt: String? = null,
    val updatedTime: String? = null,
    val certificateDate: String? = null,
    val validFrom: String? = null,
    val endTime: String? = null,
    val status: Int,
    val permitType: PermitType? = null,
    val facilities: PermitFacility? = null
)

@Serializable
data class PermitListResponse(
    val results: List<PermitItem> = emptyList()
)

@Serializable
data class PermitTypeItem(
    val permitTypeId: Int,
    val permitTypeTitle: String? = null,
    val image: String? = null,
    val formUpdatedTime: String? = null
)

@Serializable
data class PermitTypesResponse(
    val updatedTime: String? = null,
    val contents: List<PermitTypeItem> = emptyList()
)

@Serializable
data class PermitPendingActionsRequest(
    val dummy: String? = null
)

@Serializable
data class PermitPendingActionItem(
    val id: Int,
    val permitId: Int,
    val permitCode: String? = null,
    val permitType: PermitType? = null,
    val groupCode: String? = null,
    val createdAt: String? = null,
    val status: Int? = null
)

@Serializable
data class PermitPendingActionsResponse(
    val results: List<PermitPendingActionItem>? = null
)

@Serializable
data class PermitContentRequest(
    val id: Int
)

@Serializable
data class PermitContentItem(
    val id: Int,
    val title: String? = null
)

@Serializable
data class PermitContentData(
    val certificateValidity: List<PermitContentItem>? = null,
    val generalConditions: List<PermitContentItem>? = null
)

@Serializable
data class PermitExcelRequest(
    val searchKey: String = "",
    val sortBy: Int = 1,
    val projectIds: List<Int>? = null,
    val authorizer: List<Int>? = null,
    val requestor: List<Int>? = null,
    val hseAssigned: List<Int>? = null,
    val status: List<Int>? = null,
    val permitTypes: List<Int>? = null,
    val openDate: String? = null,
    val closeDate: String? = null,
    val validity: Int? = null
)

@Serializable
data class PermitExcelResponseData(
    val excelUrl: String
)


