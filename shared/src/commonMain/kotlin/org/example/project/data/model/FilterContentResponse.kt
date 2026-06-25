package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FilterContentResponse(
    @SerialName("hasError") val hasError: Boolean,
    @SerialName("errorCode") val errorCode: Int,
    @SerialName("message") val message: String,
    @SerialName("response") val response: FilterContentData? = null
)

@Serializable
data class FilterContentData(
    @SerialName("projects") val projects: List<FilterProject> = emptyList(),
    @SerialName("responsiblePersons") val responsiblePersons: List<FilterResponsiblePerson> = emptyList(),
    @SerialName("observers") val observers: List<FilterResponsiblePerson> = emptyList()
)

@Serializable
data class FilterProject(
    @SerialName("groupId") val groupId: String = "",
    @SerialName("groupName") val groupName: String = "",
    @SerialName("groupCode") val groupCode: String = "",
    @SerialName("groupImage") val groupImage: String = ""
)

@Serializable
data class FilterResponsiblePerson(
    @SerialName("userId") val userId: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("image") val image: String = ""
)

@Serializable
data class AppFilterState(
    val selectedStatuses: List<String> = emptyList(),
    val selectedProjects: List<FilterProject> = emptyList(),
    val noProjectSelected: Boolean = false,
    val selectedResponsiblePersons: List<FilterResponsiblePerson> = emptyList(),
    val selectedObservers: List<FilterResponsiblePerson> = emptyList(),
    val dateOpenMillis: Long? = null,
    val dateCloseMillis: Long? = null
) {
    fun isEmpty(): Boolean {
        return selectedStatuses.isEmpty() &&
                selectedProjects.isEmpty() &&
                !noProjectSelected &&
                selectedResponsiblePersons.isEmpty() &&
                selectedObservers.isEmpty() &&
                dateOpenMillis == null &&
                dateCloseMillis == null
    }
}
