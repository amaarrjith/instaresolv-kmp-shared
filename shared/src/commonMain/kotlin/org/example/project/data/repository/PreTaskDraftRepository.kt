package org.example.project.data.repository

import org.example.project.data.model.CreatePreTaskDraftRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.PreTaskDraft

class PreTaskDraftRepository(private val database: AppDatabase) {
    fun saveDraft(request: CreatePreTaskDraftRequest) {
        val idValue = if (request.id == 0L) null else request.id
        database.appDatabaseQueries.insertPreTaskDraft(
            id = idValue,
            facilitiesId = request.facilitiesId?.toLong(),
            projectJson = request.projectJson,
            dateMillis = request.dateMillis,
            startTime = request.startTime,
            endTime = request.endTime,
            msraReference = request.msraReference,
            permitReference = request.permitReference,
            taskTitle = request.taskTitle,
            stepByStepAccount = request.stepByStepAccount,
            contentsJson = request.contentsJson,
            questionsJson = request.questionsJson,
            questionAnswersJson = request.questionAnswersJson,
            customQuestionsJson = request.customQuestionsJson,
            attendeesJson = request.attendeesJson,
            evidencesJson = request.evidencesJson,
            selectedNotifyPersonJson = request.selectedNotifyPersonJson,
            reportedBy = request.reportedBy,
            createdAt = request.createdAt,
            userId = request.userId.toLong()
        )
    }

    fun getAllDrafts(userId: Int): List<PreTaskDraft> {
        return database.appDatabaseQueries.getPreTaskDraftsByUserId(userId.toLong()).executeAsList()
    }

    fun getDraftById(id: Long): PreTaskDraft? {
        return database.appDatabaseQueries.getPreTaskDraftById(id).executeAsOneOrNull()
    }

    fun deleteDraft(id: Long) {
        database.appDatabaseQueries.deletePreTaskDraft(id)
    }
}
