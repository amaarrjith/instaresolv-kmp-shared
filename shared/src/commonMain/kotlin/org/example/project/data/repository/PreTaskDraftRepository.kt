package org.example.project.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.example.project.data.model.CreatePreTaskDraftRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.PreTaskDraft

class PreTaskDraftRepository(private val database: AppDatabase) {
    suspend fun saveDraft(request: CreatePreTaskDraftRequest) = withContext(Dispatchers.IO) {
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

    suspend fun getAllDrafts(userId: Int): List<PreTaskDraft> = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getPreTaskDraftsByUserId(userId.toLong()).executeAsList()
    }

    suspend fun getDraftById(id: Long): PreTaskDraft? = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getPreTaskDraftById(id).executeAsOneOrNull()
    }

    suspend fun deleteDraft(id: Long) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.deletePreTaskDraft(id)
    }
}
