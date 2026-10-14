package org.example.project.utilites

import org.example.project.data.model.*

object DetailTextFormatter {

    fun formatObservation(detail: ObservationDetailResponse): String {
        return buildString {
            appendLine("Observation Details")
            appendLine("-------------------")
            detail.observationTitle?.takeIf { it.isNotBlank() }?.let { appendLine("Title: $it") }
            detail.reportedBy?.takeIf { it.isNotBlank() }?.let { appendLine("Reported By: $it") }
            detail.group?.groupName?.takeIf { it.isNotBlank() }?.let { appendLine("Project/Facility: $it") }
            detail.location?.takeIf { it.isNotBlank() }?.let { appendLine("Location: $it") }
            if (!detail.date.isNullOrBlank() || !detail.time.isNullOrBlank()) {
                val dt = listOfNotNull(detail.date, detail.time).joinToString(" ")
                appendLine("Date & Time: $dt")
            }
            detail.responsiblePersonName?.takeIf { it.isNotBlank() }?.let { appendLine("Responsible Person: $it") }
            val desc = detail.translatedDescription?.takeIf { it.isNotBlank() } ?: detail.description
            desc?.takeIf { it.isNotBlank() }?.let {
                appendLine("\nDescription:")
                appendLine(it)
            }
        }.trimEnd()
    }

    fun formatIncident(detail: IncidentDetailResponse): String {
        return buildString {
            appendLine("Incident Details")
            appendLine("----------------")
            detail.id?.let { appendLine("Reference ID: #$it") }
            detail.reportedBy?.takeIf { it.isNotBlank() }?.let { appendLine("Reported By: $it") }
            detail.facilities?.groupName?.takeIf { it.isNotBlank() }?.let { appendLine("Project/Facility: $it") }
            detail.incidentLocation?.takeIf { it.isNotBlank() }?.let { appendLine("Location: $it") }
            if (!detail.incidentDate.isNullOrBlank() || !detail.incidentTime.isNullOrBlank()) {
                val dt = listOfNotNull(detail.incidentDate, detail.incidentTime).joinToString(" ")
                appendLine("Date & Time: $dt")
            }
            val desc = detail.translatedDescription?.takeIf { it.isNotBlank() } ?: detail.description
            desc?.takeIf { it.isNotBlank() }?.let {
                appendLine("\nDescription:")
                appendLine(it)
            }
            val corr = detail.translatedCorrections?.takeIf { it.isNotBlank() } ?: detail.corrections
            corr?.takeIf { it.isNotBlank() }?.let {
                appendLine("\nCorrections / Action Taken:")
                appendLine(it)
            }
        }.trimEnd()
    }

    fun formatInspection(detail: InspectionDetailResponse): String {
        return buildString {
            appendLine("Inspection Details")
            appendLine("------------------")
            appendLine("Reference ID: #${detail.id}")
            detail.modelNumber?.takeIf { it.isNotBlank() }?.let { appendLine("Equipment / Model: $it") }
            detail.inspectedBy?.takeIf { it.isNotBlank() }?.let { appendLine("Inspected By: $it") }
            detail.facilities?.groupName?.takeIf { it.isNotBlank() }?.let { appendLine("Project/Facility: $it") }
            detail.location?.takeIf { it.isNotBlank() }?.let { appendLine("Location: $it") }
            detail.inspectionDate?.takeIf { it.isNotBlank() }?.let { appendLine("Date: $it") }
            detail.subContractor?.takeIf { it.isNotBlank() }?.let { appendLine("Subcontractor: $it") }
            val desc = detail.translatedDescription?.takeIf { it.isNotBlank() } ?: detail.description
            desc?.takeIf { it.isNotBlank() }?.let {
                appendLine("\nDescription:")
                appendLine(it)
            }
            val notes = detail.translatedNotes?.takeIf { it.isNotBlank() } ?: detail.notes
            notes?.takeIf { it.isNotBlank() }?.let {
                appendLine("\nNotes:")
                appendLine(it)
            }
        }.trimEnd()
    }

    fun formatViolation(detail: ViolationData): String {
        return buildString {
            appendLine("Violation Details")
            appendLine("-----------------")
            appendLine("Reference ID: #${detail.id}")
            detail.employeeName?.takeIf { it.isNotBlank() }?.let { appendLine("Employee Name: $it") }
            detail.employeeId?.takeIf { it.isNotBlank() }?.let { appendLine("Employee ID: $it") }
            detail.reportedBy?.takeIf { it.isNotBlank() }?.let { appendLine("Reported By: $it") }
            detail.facilities?.groupName?.takeIf { it.isNotBlank() }?.let { appendLine("Project/Facility: $it") }
            detail.location?.takeIf { it.isNotBlank() }?.let { appendLine("Location: $it") }
            detail.violationDate?.takeIf { it.isNotBlank() }?.let { appendLine("Date: $it") }
            val desc = detail.translatedDescription?.takeIf { it.isNotBlank() } ?: detail.description
            desc?.takeIf { it.isNotBlank() }?.let {
                appendLine("\nDescription:")
                appendLine(it)
            }
        }.trimEnd()
    }

    fun formatLessonLearned(detail: LessonLearnedDetailResponseData): String {
        return buildString {
            appendLine("Lesson Learned Details")
            appendLine("----------------------")
            appendLine("Reference ID: #${detail.id}")
            detail.title?.takeIf { it.isNotBlank() }?.let { appendLine("Title: $it") }
            detail.reportedBy?.takeIf { it.isNotBlank() }?.let { appendLine("Reported By: $it") }
            detail.facilities?.groupName?.takeIf { it.isNotBlank() }?.let { appendLine("Project/Facility: $it") }
            detail.createdAt?.takeIf { it.isNotBlank() }?.let { appendLine("Date: $it") }
            val desc = detail.translatedDescription?.takeIf { it.isNotBlank() } ?: detail.description
            desc?.takeIf { it.isNotBlank() }?.let {
                appendLine("\nDescription:")
                appendLine(it)
            }
        }.trimEnd()
    }

    fun formatToolBoxTalk(detail: ToolBoxTalkItem): String {
        return buildString {
            appendLine("Toolbox Talk Details")
            appendLine("--------------------")
            appendLine("Reference ID: #${detail.id}")
            val topic = detail.translatedTopic?.takeIf { it.isNotBlank() } ?: detail.topic
            topic?.takeIf { it.isNotBlank() }?.let { appendLine("Topic: $it") }
            detail.reportedBy?.takeIf { it.isNotBlank() }?.let { appendLine("Conducted By: $it") }
            detail.facilities?.groupName?.takeIf { it.isNotBlank() }?.let { appendLine("Project/Facility: $it") }
            detail.date?.takeIf { it.isNotBlank() }?.let { appendLine("Date: $it") }
            if (!detail.startTime.isNullOrBlank() || !detail.endTime.isNullOrBlank()) {
                val times = listOfNotNull(detail.startTime, detail.endTime).joinToString(" - ")
                appendLine("Time: $times")
            }
            if (!detail.discussionPoints.isNullOrEmpty()) {
                appendLine("\nDiscussion Points:")
                detail.discussionPoints.forEachIndexed { index, dp ->
                    appendLine("${index + 1}. ${dp.point}")
                }
            }
        }.trimEnd()
    }

    fun formatPreTask(detail: PreTaskDetailResponseData): String {
        return buildString {
            appendLine("Pre-Task Details")
            appendLine("----------------")
            appendLine("Reference ID: #${detail.id}")
            detail.taskTitle?.takeIf { it.isNotBlank() }?.let { appendLine("Task: $it") }
            detail.reportedBy?.takeIf { it.isNotBlank() }?.let { appendLine("Reported By: $it") }
            detail.facilities?.groupName?.takeIf { it.isNotBlank() }?.let { appendLine("Project/Facility: $it") }
            detail.msraReference?.takeIf { it.isNotBlank() }?.let { appendLine("MSRA Ref: $it") }
            detail.permitReference?.takeIf { it.isNotBlank() }?.let { appendLine("Permit Ref: $it") }
            detail.date?.takeIf { it.isNotBlank() }?.let { appendLine("Date: $it") }
            val notes = detail.translatedNotes?.takeIf { it.isNotBlank() } ?: detail.notes
            notes?.takeIf { it.isNotBlank() }?.let {
                appendLine("\nNotes:")
                appendLine(it)
            }
        }.trimEnd()
    }

    fun formatPermit(detail: PermitDetailData): String {
        return buildString {
            appendLine("Permit to Work Details")
            appendLine("----------------------")
            detail.permitCode?.takeIf { it.isNotBlank() }?.let { appendLine("Permit Code: $it") }
            detail.permitType?.permitTypeTitle?.takeIf { it.isNotBlank() }?.let { appendLine("Type: $it") }
            detail.facility?.groupName?.takeIf { it.isNotBlank() }?.let { appendLine("Project/Facility: $it") }
            detail.permitRequestedUser?.name?.takeIf { it.isNotBlank() }?.let { appendLine("Requested By: $it") }
            detail.createdAt?.takeIf { it.isNotBlank() }?.let { appendLine("Date: $it") }
        }.trimEnd()
    }
}
