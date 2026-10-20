package org.example.project.utilites

import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.example.project.data.model.*
import org.example.project.ui.IncidentType
import org.jetbrains.compose.resources.getString
import instaresolv.shared.generated.resources.*

object DetailTextFormatter {

    // MARK: - Helper Functions

    private fun safeText(value: String?, defaultText: String = "N/A"): String {
        val trimmed = value?.trim()
        return if (trimmed.isNullOrEmpty()) defaultText else trimmed
    }

    private fun cleanText(value: String?): String? {
        val trimmed = value?.trim()
        return if (trimmed.isNullOrEmpty()) null else trimmed
    }

    private suspend fun imageCountText(count: Int): String {
        return when (count) {
            0 -> getString(Res.string.no_images)
            1 -> getString(Res.string.one_image_attached)
            else -> getString(Res.string.images_attached_count, count)
        }
    }

    private fun formatDate(value: String?, defaultText: String = "N/A"): String {
        val text = cleanText(value) ?: return defaultText

        // Try parsing ISO Instant first
        try {
            val instant = Instant.parse(text)
            val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            val day = dt.day.toString().padStart(2, '0')
            val month = dt.month.number.toString().padStart(2, '0')
            val year = dt.year.toString().padStart(4, '0')
            return "$day-$month-$year"
        } catch (_: Exception) {}

        // Match yyyy-MM-dd
        val ymdRegex = Regex("""^(\d{4})-(\d{1,2})-(\d{1,2})""")
        ymdRegex.find(text)?.let {
            val (year, month, day) = it.destructured
            return "${day.padStart(2, '0')}-${month.padStart(2, '0')}-$year"
        }

        // Match dd-MM-yyyy or dd/MM/yyyy
        val dmyRegex = Regex("""^(\d{1,2})[-/](\d{1,2})[-/](\d{4})""")
        dmyRegex.find(text)?.let {
            val (day, month, year) = it.destructured
            return "${day.padStart(2, '0')}-${month.padStart(2, '0')}-$year"
        }

        return text
    }

    private fun formatTime(value: String?): String {
        val text = cleanText(value) ?: return ""
        val timeRegex = Regex("""(\d{1,2}):(\d{2})(?::\d{2})?""")
        timeRegex.find(text)?.let {
            val (hour, min) = it.destructured
            return "${hour.padStart(2, '0')}:$min"
        }
        return text
    }

    private fun convertUtcToLocal(utcString: String?): String? {
        val text = cleanText(utcString) ?: return null
        return try {
            val instant = try {
                Instant.parse(text)
            } catch (_: Exception) {
                val cleaned = text.replace(" ", "T").let {
                    if (!it.endsWith("Z") && !it.contains("+")) "${it}Z" else it
                }
                Instant.parse(cleaned)
            }
            val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            val day = dt.day.toString().padStart(2, '0')
            val month = dt.month.number.toString().padStart(2, '0')
            val year = dt.year.toString().padStart(4, '0')
            val hour = dt.hour.toString().padStart(2, '0')
            val minute = dt.minute.toString().padStart(2, '0')
            "$day-$month-$year $hour:$minute"
        } catch (_: Exception) {
            text
        }
    }

    // MARK: - 1. Observation
    suspend fun formatObservation(detail: ObservationDetailResponse, id: Int? = null): String {
        val na = getString(Res.string.na)
        val titleText = safeText(detail.observationTitle, na)
        val reportedByText = safeText(detail.reportedBy, na)
        val reportedDateText = safeText(formatDate(detail.date, na), na)
        val projectText = safeText(detail.group?.groupName, na)
        val statusText = when (detail.status) {
            1 -> getString(Res.string.open)
            2 -> getString(Res.string.pending_closeout)
            3 -> getString(Res.string.closed)
            else -> na
        }
        val locationText = safeText(detail.location, na)

        val responsiblePersonText = if (detail.groupSpecified == true) {
            safeText(detail.responsiblePerson?.name, na)
        } else {
            safeText(detail.responsiblePersonName, na)
        }

        val imageCount = detail.imageDescription?.size ?: 0
        val imagesText = imageCountText(imageCount)
        val descriptionText = safeText(detail.translatedDescription?.takeIf { it.isNotBlank() } ?: detail.description, na)

        val closeoutText = if (detail.status == 2 || detail.status == 3) {
            val closeDateText = safeText(formatDate(detail.closeDetails?.date, na), na)
            val closedByText = safeText(detail.closeDetails?.closedBy?.name, na)
            val closeDescriptionText = safeText(detail.closeDetails?.closeDescription, na)

            "\n\n*--- ${getString(Res.string.close_out)} ---*\n" +
            "*${getString(Res.string.date_closed)} :* $closeDateText\n" +
            "*${getString(Res.string.closedBy).uppercase()} :* $closedByText\n" +
            "*${getString(Res.string.closeout_description)} :* $closeDescriptionText"
        } else {
            ""
        }

        val headerId = if (id != null) " $id" else ""

        return """
        *--- ${getString(Res.string.observation_report)}$headerId ---*
        *${getString(Res.string.title).uppercase()} :* $titleText
        *${getString(Res.string.reportedBy).uppercase()} :* $reportedByText
        *${getString(Res.string.date_reported)} :* $reportedDateText
        *${getString(Res.string.project).uppercase()} :* $projectText
        *${getString(Res.string.report_status)} :* $statusText
        *${getString(Res.string.location).uppercase()} :* $locationText
        *${getString(Res.string.responsiblePerson).uppercase()} :* $responsiblePersonText
        *${getString(Res.string.description).uppercase()} :* $descriptionText$closeoutText
        *${getString(Res.string.images).uppercase()} :* $imagesText
        """.trimIndent()
    }

    // MARK: - 2. Incident
    suspend fun formatIncident(detail: IncidentDetailResponse): String {
        val na = getString(Res.string.na)
        val incidentTypesText = detail.incidentType?.mapNotNull { typeId ->
            IncidentType.fromId(typeId)?.title
        }?.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: na

        val nameLabel = getString(Res.string.name)
        val codeLabel = getString(Res.string.code)
        val companyLabel = getString(Res.string.company)
        val professionLabel = getString(Res.string.profession)

        val injuredEmployeesText = detail.injuredEmployees?.takeIf { it.isNotEmpty() }?.joinToString("\n\n") { emp ->
            "$nameLabel : ${safeText(emp.employeeName, na)}\n" +
            "$codeLabel : ${safeText(emp.employeeCode, na)}\n" +
            "$companyLabel : ${safeText(emp.companyName, na)}\n" +
            "$professionLabel : ${safeText(emp.profession, na)}"
        } ?: na

        val facilityName = safeText(detail.facilities?.groupName, na)
        val imageCount = detail.images?.size ?: 0
        val imagesText = imageCountText(imageCount)

        val incidentDateText = safeText(formatDate(detail.incidentDate, na), na)
        val incidentTimeText = safeText(formatTime(detail.incidentTime), na)

        val locationText = safeText(detail.incidentLocation, na)
        val descriptionText = safeText(detail.translatedDescription?.takeIf { it.isNotBlank() } ?: detail.description, na)
        val correctionsText = safeText(detail.translatedCorrections?.takeIf { it.isNotBlank() } ?: detail.corrections, na)

        val reportedOnText = convertUtcToLocal(detail.createdAt) ?: na
        val reportedByText = safeText(detail.reportedBy, na)

        val headerId = if (detail.id != null) " ${detail.id}" else ""

        return """
        *--- ${getString(Res.string.incident_report)}$headerId ---*
        *${getString(Res.string.incidentDate).uppercase()} :* $incidentDateText
        *${getString(Res.string.incidentTime).uppercase()} :* $incidentTimeText
        *${getString(Res.string.incident_types)} :* $incidentTypesText
        *${getString(Res.string.project).uppercase()} :* $facilityName
        *${getString(Res.string.location).uppercase()} :* $locationText

        *${getString(Res.string.injured_employees)} :*
        $injuredEmployeesText

        *${getString(Res.string.description).uppercase()} :*
        $descriptionText

        *${getString(Res.string.corrections)} :*
        $correctionsText

        *${getString(Res.string.images).uppercase()} :* $imagesText
        *${getString(Res.string.reported_on)} :* $reportedOnText
        *${getString(Res.string.reportedBy).uppercase()} :* $reportedByText
        """.trimIndent()
    }

    // MARK: - 3. Inspection
    suspend fun formatInspection(detail: InspectionDetailResponse): String {
        val na = getString(Res.string.na)
        val showingIds = setOf(4, 5, 6, 7, 8)
        val isShowingContent = detail.auditItem?.auditItemId in showingIds

        val titleText = safeText(detail.auditItem?.auditItemTitle, na)
        val inspectedByText = safeText(detail.inspectedBy, na)
        val modelNumberText = safeText(detail.modelNumber, na)
        val inspectionDateText = safeText(formatDate(detail.inspectionDate, na), na)
        val facilityText = safeText(detail.facilities?.groupName, na)
        val locationText = safeText(detail.location, na)
        val descriptionText = safeText(detail.translatedDescription?.takeIf { it.isNotBlank() } ?: detail.description, na)
        val notesText = safeText(detail.translatedNotes?.takeIf { it.isNotBlank() } ?: detail.notes, na)
        val subContractorText = safeText(detail.subContractor, na)
        val equipmentSourceText = safeText(detail.equipmentSource, na)

        val yesText = getString(Res.string.yes)
        val noText = getString(Res.string.no)

        val equipmentLines = detail.staticEquipment?.takeIf { it.isNotEmpty() }?.joinToString("\n") { eq ->
            val title = safeText(eq.title, na)
            val selectedValueText = when (eq.selectedValue) {
                1 -> yesText
                2 -> noText
                else -> na
            }
            "$title: $selectedValueText"
        } ?: ""

        val imageCount = detail.images?.size ?: 0
        val imagesText = imageCountText(imageCount)

        val reportedOnText = convertUtcToLocal(detail.createdAt) ?: na
        val reportedByText = safeText(detail.inspectedBy, na)

        val conditionalContent = if (isShowingContent) {
            "\n*${getString(Res.string.modelNumber).uppercase()} :* $modelNumberText\n" +
            "*${getString(Res.string.sub_contractor)} :* $subContractorText\n" +
            "*${getString(Res.string.equipmentSource).uppercase()} :* $equipmentSourceText"
        } else {
            ""
        }

        val label = "$titleText ${getString(Res.string.fields)}".uppercase()
        val equipmentBlock = if (equipmentLines.isNotEmpty()) {
            "\n\n*${label} :*\n$equipmentLines"
        } else {
            ""
        }

        return """
        *--- ${getString(Res.string.audit_inspections_report)} ${detail.id} ---*
        *${getString(Res.string.title).uppercase()} :* $titleText
        *${getString(Res.string.inspected_by)} :* $inspectedByText
        *${getString(Res.string.inspection_date)} :* $inspectionDateText
        *${getString(Res.string.project).uppercase()} :* $facilityText
        *${getString(Res.string.location).uppercase()} :* $locationText
        *${getString(Res.string.description).uppercase()} :* $descriptionText
        *${getString(Res.string.notes).uppercase()} :* $notesText$conditionalContent$equipmentBlock

        *${getString(Res.string.images).uppercase()} :* $imagesText
        *${getString(Res.string.reported_on)} :* $reportedOnText
        *${getString(Res.string.reportedBy).uppercase()} :* $reportedByText
        """.trimIndent()
    }

    // MARK: - 4. Violation
    suspend fun formatViolation(detail: ViolationData): String {
        val na = getString(Res.string.na)
        val employeeName = safeText(detail.employeeName, na)
        val employeeId = safeText(detail.employeeId, na)
        val violationDate = safeText(formatDate(detail.violationDate, na), na)
        val facilityName = safeText(detail.facilities?.groupName, na)
        val locationText = safeText(detail.location, na)
        val descriptionText = safeText(detail.translatedDescription?.takeIf { it.isNotBlank() } ?: detail.description, na)
        val imagesText = imageCountText(detail.images?.size ?: 0)
        val reportedOnText = convertUtcToLocal(detail.createdAt) ?: na
        val reportedByText = safeText(detail.reportedBy, na)

        return """
        *--- ${getString(Res.string.violation_report)} ${detail.id} ---*
        *${getString(Res.string.employeeName).uppercase()} :* $employeeName
        *${getString(Res.string.employeeId).uppercase()} :* $employeeId
        *${getString(Res.string.violationDate).uppercase()} :* $violationDate
        *${getString(Res.string.project).uppercase()} :* $facilityName
        *${getString(Res.string.location).uppercase()} :* $locationText
        *${getString(Res.string.description).uppercase()} :* $descriptionText
        *${getString(Res.string.images).uppercase()} :* $imagesText
        *${getString(Res.string.reported_on)} :* $reportedOnText
        *${getString(Res.string.reportedBy).uppercase()} :* $reportedByText
        """.trimIndent()
    }

    // MARK: - 5. Lesson Learned
    suspend fun formatLessonLearned(detail: LessonLearnedDetailResponseData): String {
        val na = getString(Res.string.na)
        val titleText = safeText(detail.title, na)
        val facilityName = safeText(detail.facilities?.groupName, na)
        val descriptionText = safeText(detail.translatedDescription?.takeIf { it.isNotBlank() } ?: detail.description, na)
        val imagesText = imageCountText(detail.images?.size ?: 0)
        val reportedOnText = convertUtcToLocal(detail.createdAt) ?: na
        val reportedByText = safeText(detail.reportedBy, na)

        return """
        *--- ${getString(Res.string.lesson_learned_report)} ${detail.id} ---*
        *${getString(Res.string.title).uppercase()} :* $titleText
        *${getString(Res.string.project).uppercase()} :* $facilityName
        *${getString(Res.string.description).uppercase()} :* $descriptionText
        *${getString(Res.string.images).uppercase()} :* $imagesText
        *${getString(Res.string.reported_on)} :* $reportedOnText
        *${getString(Res.string.reportedBy).uppercase()} :* $reportedByText
        """.trimIndent()
    }

    // MARK: - 6. ToolBox Talk
    suspend fun formatToolBoxTalk(detail: ToolBoxTalkItem): String {
        val na = getString(Res.string.na)
        val discussionPointsList = detail.discussionPoints?.mapNotNull { cleanText(it.translatedPoint ?: it.point) }
        val discussionText = if (discussionPointsList.isNullOrEmpty()) {
            na
        } else {
            discussionPointsList.joinToString("\n") { "• $it" }
        }

        val nameLabel = getString(Res.string.name)
        val codeLabel = getString(Res.string.code)
        val companyLabel = getString(Res.string.company)
        val professionLabel = getString(Res.string.profession)

        val attendeesText = detail.attendees?.takeIf { it.isNotEmpty() }?.joinToString("\n\n") { att ->
            "$nameLabel : ${safeText(att.employeeName, na)}\n" +
            "$codeLabel : ${safeText(att.employeeCode, na)}\n" +
            "$companyLabel : ${safeText(att.companyName, na)}\n" +
            "$professionLabel : ${safeText(att.profession, na)}"
        } ?: na

        val facilityName = safeText(detail.facilities?.groupName, na)
        val imageCount = detail.images?.size ?: 0
        val imagesText = imageCountText(imageCount)

        val dateText = safeText(formatDate(detail.date, na), na)
        val startTimeText = safeText(formatTime(detail.startTime), na)
        val endTimeText = safeText(formatTime(detail.endTime), na)
        val createdAtText = convertUtcToLocal(detail.createdAt) ?: na
        val reportedByText = safeText(detail.reportedBy, na)
        val topicText = safeText(detail.translatedTopic?.takeIf { it.isNotBlank() } ?: detail.topic, na)

        return """
        *--- ${getString(Res.string.toolbox_talk_report)} ${detail.id} ---*
        *${getString(Res.string.date).uppercase()} :* $dateText
        *${getString(Res.string.startTime).uppercase()} :* $startTimeText
        *${getString(Res.string.endTime).uppercase()} :* $endTimeText
        *${getString(Res.string.topic).uppercase()} :* $topicText
        *${getString(Res.string.discussionPoints).uppercase()} :*
        $discussionText

        *${getString(Res.string.attendees)} :*
        $attendeesText

        *${getString(Res.string.project).uppercase()} :* $facilityName
        *${getString(Res.string.images).uppercase()} :* $imagesText
        *${getString(Res.string.reported_on)} :* $createdAtText
        *${getString(Res.string.reportedBy).uppercase()} :* $reportedByText
        """.trimIndent()
    }

    // MARK: - 7. Pre-Task
    suspend fun formatPreTask(detail: PreTaskDetailResponseData): String {
        val na = getString(Res.string.na)
        val yesText = getString(Res.string.yes)
        val noText = getString(Res.string.no)
        val notApplicableText = getString(Res.string.not_applicable)

        fun answerTitle(answer: Int?): String = when (answer) {
            1 -> yesText
            2 -> noText
            3 -> notApplicableText
            else -> "-"
        }

        val topicsBuilder = StringBuilder()
        detail.contents?.forEach { content ->
            val relatedQuestions = detail.questions?.filter { it.contentId == content.id }
            if (!relatedQuestions.isNullOrEmpty()) {
                if (topicsBuilder.isNotEmpty()) topicsBuilder.append("\n")
                topicsBuilder.append("• *${safeText(content.title, na)}*")
                for (q in relatedQuestions) {
                    val ans = answerTitle(q.selectedAnswer)
                    topicsBuilder.append("\n   ◦ ${safeText(q.title, na)} — *$ans*")
                }
            }
        }
        val topicsText = if (topicsBuilder.isEmpty()) na else topicsBuilder.toString()

        val otherBuilder = StringBuilder()
        if (!detail.otherTopic.isNullOrEmpty()) {
            otherBuilder.append("• ${getString(Res.string.others_if_any)}")
            for (q in detail.otherTopic) {
                val ans = answerTitle(q.selectedAnswer)
                otherBuilder.append("\n   ◦ ${safeText(q.title, na)} — *$ans*")
            }
        }
        val otherQuestionText = if (otherBuilder.isEmpty()) na else otherBuilder.toString()

        val nameLabel = getString(Res.string.name)
        val codeLabel = getString(Res.string.code)
        val companyLabel = getString(Res.string.company)
        val professionLabel = getString(Res.string.profession)

        val attendeesText = detail.attendees?.takeIf { it.isNotEmpty() }?.joinToString("\n\n") { emp ->
            "$nameLabel : ${safeText(emp.employeeName, na)}\n" +
            "$codeLabel : ${safeText(emp.employeeCode, na)}\n" +
            "$companyLabel : ${safeText(emp.companyName, na)}\n" +
            "$professionLabel : ${safeText(emp.profession, na)}"
        } ?: na

        val facilityName = safeText(detail.facilities?.groupName, na)
        val imageCount = detail.images?.size ?: 0
        val imagesText = imageCountText(imageCount)

        val dateText = safeText(formatDate(detail.date, na), na)
        val startTimeText = formatTime(detail.startTime)
        val endTimeText = formatTime(detail.endTime)
        val createdAtText = convertUtcToLocal(detail.createdAt) ?: na
        val reportedByText = safeText(detail.reportedBy, na)
        val notesText = safeText(detail.translatedNotes?.takeIf { it.isNotBlank() } ?: detail.notes, na)

        return """
        *--- ${getString(Res.string.pre_task_briefing_report)} ${detail.id} ---*
        *${getString(Res.string.date).uppercase()} :* $dateText
        *${getString(Res.string.startTime).uppercase()} :* $startTimeText
        *${getString(Res.string.endTime).uppercase()} :* $endTimeText
        *${getString(Res.string.taskTitle).uppercase()} :* ${safeText(detail.taskTitle, na)}
        *${getString(Res.string.permitReference).uppercase()} :* ${safeText(detail.permitReference, na)}
        *${getString(Res.string.msra_reference)} :* ${safeText(detail.msraReference, na)}

        *${getString(Res.string.topics_of_discussion)} :*
        $topicsText

        *${getString(Res.string.other_questions)} :*
        $otherQuestionText

        *${getString(Res.string.attendees)} :*
        $attendeesText

        *${getString(Res.string.project).uppercase()} :* $facilityName
        *${getString(Res.string.images).uppercase()} :* $imagesText
        *${getString(Res.string.step_by_step_account)} :*
        $notesText
        *${getString(Res.string.reported_on)} :* $createdAtText
        *${getString(Res.string.reportedBy).uppercase()} :* $reportedByText
        """.trimIndent()
    }

    // MARK: - 8. Permit To Work
    suspend fun formatPermit(detail: PermitDetailData): String {
        val na = getString(Res.string.na)

        fun joinedText(parts: List<String?>, separator: String = " | "): String? {
            val nonNull = parts.mapNotNull { cleanText(it) }
            return if (nonNull.isEmpty()) null else nonNull.joinToString(separator)
        }

        fun line(label: String, value: String?): String? {
            val v = cleanText(value) ?: return null
            return "*$label :* $v"
        }

        fun section(title: String, lines: List<String?>): String? {
            val visibleLines = lines.mapNotNull { it }
            if (visibleLines.isEmpty()) return null
            return "*--- $title ---*\n${visibleLines.joinToString("\n")}"
        }

        fun userSummary(user: PermitDetailUser?): String? {
            if (user == null) return null
            return joinedText(listOf(user.name, user.email, user.company))
        }

        suspend fun permitImageCountText(images: List<PermitImage>?): String? {
            val count = images?.count { !it.image.isNullOrBlank() } ?: 0
            if (count == 0) return null
            return if (count == 1) getString(Res.string.one_image_attached) else getString(Res.string.images_attached_count, count)
        }

        suspend fun permitStatusTitle(status: Int?): String? {
            val permitStatus = status?.let { PermitStatus.fromValue(it) } ?: return null
            return getString(permitStatus.stringRes)
        }

        val permitCode = cleanText(detail.permitCode) ?: "${detail.permitId ?: ""}".trim()
        val project = joinedText(listOf(
            detail.certificateValidity?.project?.groupName ?: detail.facility?.groupName,
            detail.certificateValidity?.project?.groupCode ?: detail.facility?.groupCode
        ))

        val sections = mutableListOf<String>()

        val overviewSection = section(
            title = "${getString(Res.string.permit_to_work_report)} $permitCode".trim(),
            lines = listOf(
                line(getString(Res.string.permit_type), detail.permitType?.permitTypeTitle),
                line(getString(Res.string.status).uppercase(), permitStatusTitle(detail.permitStatus)),
                line(getString(Res.string.project).uppercase(), project),
                line(getString(Res.string.requestedBy).uppercase(), userSummary(detail.permitRequestedUser)),
                line(getString(Res.string.reported_on), convertUtcToLocal(detail.createdAt))
            )
        )
        overviewSection?.let { sections.add(it) }

        val cv = detail.certificateValidity
        if (cv != null) {
            val authPerson = joinedText(listOf(cv.authorizedPerson?.name, cv.authorizedPerson?.email))
            val cvSection = section(
                title = getString(Res.string.certificate_validity_header),
                lines = listOf(
                    line(getString(Res.string.contractorName).uppercase(), cv.contractorName),
                    line(getString(Res.string.certificate_date), formatDate(cv.certificateDate, na)),
                    line(getString(Res.string.validFrom).uppercase(), formatTime(cv.validFrom)),
                    line(getString(Res.string.endTime).uppercase(), formatTime(cv.endTime)),
                    line(getString(Res.string.authorizedPerson).uppercase(), authPerson),
                    line(getString(Res.string.requestorName).uppercase(), cv.requestContractor),
                    line(getString(Res.string.date).uppercase(), formatDate(cv.requestDate, na)),
                    line(getString(Res.string.time).uppercase(), formatTime(cv.requestTime))
                )
            )
            cvSection?.let { sections.add(it) }

            val checklistItems = cv.certificateValiditySections?.mapNotNull { item ->
                val ans = cleanText(item.answer) ?: return@mapNotNull null
                "• ${item.title} : $ans"
            } ?: emptyList()
            section(getString(Res.string.certificate_validity_sections), checklistItems)?.let { sections.add(it) }

            val yesText = getString(Res.string.yes)
            val noText = getString(Res.string.no)
            val notApplicableText = getString(Res.string.not_applicable)

            val generalConditionItems = cv.generalConditions?.mapNotNull { item ->
                val ans = when (item.answer) {
                    1 -> yesText
                    2 -> noText
                    3 -> notApplicableText
                    else -> return@mapNotNull null
                }
                val remarks = cleanText(item.remarks)
                if (remarks != null) {
                    "• ${item.title} : $ans ($remarks)"
                } else {
                    "• ${item.title} : $ans"
                }
            } ?: emptyList()
            section(getString(Res.string.general_conditions), generalConditionItems)?.let { sections.add(it) }
        }

        val scAuth = detail.subContractorAuthorization
        if (scAuth != null) {
            val alnasr = joinedText(listOf(scAuth.alnasrAuthorizer?.name, scAuth.alnasrAuthorizer?.email))
            section(
                title = getString(Res.string.subContractorAuthorisation).uppercase(),
                lines = listOf(
                    line(getString(Res.string.name).uppercase(), scAuth.subContractorAuthorizerName),
                    line(getString(Res.string.instaresolvAuthorizerName).uppercase(), alnasr),
                    line(getString(Res.string.date).uppercase(), formatDate(scAuth.authorizationDate, na)),
                    line(getString(Res.string.time).uppercase(), formatTime(scAuth.authorizationTime)),
                    line(getString(Res.string.signature).uppercase(), if (cleanText(scAuth.signatureImageUrl) != null) getString(Res.string.attached) else null)
                )
            )?.let { sections.add(it) }
        }

        val authReq = detail.authorizationRequest
        if (authReq != null) {
            val hse = joinedText(listOf(authReq.responsibleHSEPerson?.name, authReq.responsibleHSEPerson?.email))
            section(
                title = getString(Res.string.authorizationRequest).uppercase(),
                lines = listOf(
                    line(getString(Res.string.authorizerName).uppercase(), authReq.authorizerName),
                    line(getString(Res.string.msra_number), authReq.msraNumber),
                    line(getString(Res.string.responsible_hse_person), hse),
                    line(getString(Res.string.date).uppercase(), formatDate(authReq.authorizedDate, na)),
                    line(getString(Res.string.time).uppercase(), formatTime(authReq.authorizedTime)),
                    line(getString(Res.string.additionalPrecautions).uppercase(), authReq.notes),
                    line(getString(Res.string.signature).uppercase(), if (cleanText(authReq.signatureImageUrl) != null) getString(Res.string.attached) else null)
                )
            )?.let { sections.add(it) }
        }

        val reqClosure = detail.requestForCertificateClosure
        if (reqClosure != null) {
            section(
                title = getString(Res.string.request_permit_closure_header),
                lines = listOf(
                    line(getString(Res.string.remarks).uppercase(), reqClosure.remarks),
                    line(getString(Res.string.date).uppercase(), formatDate(reqClosure.requestDate, na)),
                    line(getString(Res.string.time).uppercase(), formatTime(reqClosure.requestTime)),
                    line(getString(Res.string.images).uppercase(), permitImageCountText(reqClosure.images)),
                    line(getString(Res.string.signature).uppercase(), if (cleanText(reqClosure.signatureImageUrl) != null) getString(Res.string.attached) else null)
                )
            )?.let { sections.add(it) }
        }

        val scClosure = detail.subContractorClosure
        if (scClosure != null) {
            section(
                title = getString(Res.string.subContractorClosure).uppercase(),
                lines = listOf(
                    line(getString(Res.string.name).uppercase(), scClosure.authorizerName),
                    line(getString(Res.string.date).uppercase(), formatDate(scClosure.closureDate, na)),
                    line(getString(Res.string.time).uppercase(), formatTime(scClosure.closureTime)),
                    line(getString(Res.string.signature).uppercase(), if (cleanText(scClosure.signatureImageUrl) != null) getString(Res.string.attached) else null)
                )
            )?.let { sections.add(it) }
        }

        val certClosure = detail.certificateClosure
        if (certClosure != null) {
            section(
                title = getString(Res.string.permitClosure).uppercase(),
                lines = listOf(
                    line(getString(Res.string.contractorName).uppercase(), certClosure.contractorName),
                    line(getString(Res.string.date).uppercase(), formatDate(certClosure.closureDate, na)),
                    line(getString(Res.string.time).uppercase(), formatTime(certClosure.closureTime)),
                    line(getString(Res.string.signature).uppercase(), if (cleanText(certClosure.signatureImageUrl) != null) getString(Res.string.attached) else null)
                )
            )?.let { sections.add(it) }
        }

        val cancelledUsers = detail.permitCancelledUsers?.mapNotNull { userSummary(it) } ?: emptyList()
        if (cancelledUsers.isNotEmpty()) {
            section(getString(Res.string.cancelled_users), cancelledUsers.map { "• $it" })?.let { sections.add(it) }
        }

        val suspendedUsers = detail.permitSuspendedUsers?.mapNotNull { userSummary(it) } ?: emptyList()
        if (suspendedUsers.isNotEmpty()) {
            section(getString(Res.string.suspended_users), suspendedUsers.map { "• $it" })?.let { sections.add(it) }
        }

        for (r in detail.permitReactivationDetails ?: emptyList()) {
            val actioned = userSummary(r.actionedBy)
            section(
                title = getString(Res.string.reactivate_permit_header),
                lines = listOf(
                    line(getString(Res.string.remarks).uppercase(), r.remarks),
                    line(getString(Res.string.reactivated_by), actioned),
                    line(getString(Res.string.images).uppercase(), permitImageCountText(r.images))
                )
            )?.let { sections.add(it) }
        }

        return sections.joinToString("\n\n")
    }
}
