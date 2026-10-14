package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerateIncidentPdfRequest(
    val incidentId: Int,
    val id: Int = incidentId
)

@Serializable
data class GenerateObservationPdfRequest(
    val observationId: Int,
    val id: Int = observationId
)

@Serializable
data class GenerateViolationPdfRequest(
    val violationId: Int,
    val id: Int = violationId
)

@Serializable
data class GenerateLessonLearnedPdfRequest(
    val lessonId: Int,
    val id: Int = lessonId
)

@Serializable
data class GeneratePermitPdfRequest(
    val permitId: Int,
    val id: Int = permitId
)

@Serializable
data class GenerateInspectionPdfRequest(
    val id: Int,
    val inspectionId: Int = id,
    val inspection_id: Int = id
)

@Serializable
data class GenerateToolBoxTalkPdfRequest(
    val toolBoxId: Int
)

@Serializable
data class GeneratePreTaskPdfRequest(
    val id: Int,
    val preTaskId: Int = id,
    val pretaskId: Int = id,
    val pre_task_id: Int = id
)
