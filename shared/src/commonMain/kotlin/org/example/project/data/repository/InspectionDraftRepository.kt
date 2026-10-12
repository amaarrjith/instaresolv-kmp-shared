package org.example.project.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.example.project.data.model.CreateInspectionDraftRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.InspectionDraft

class InspectionDraftRepository(private val database: AppDatabase) {
    suspend fun saveDraft(request: CreateInspectionDraftRequest) = withContext(Dispatchers.IO) {
        val idValue = if (request.id == 0L) null else request.id
        database.appDatabaseQueries.insertInspectionDraft(
            id = idValue,
            facilitiesId = request.facilitiesId?.toLong(),
            projectJson = request.projectJson,
            inspectionTypeId = request.inspectionTypeId.toLong(),
            inspectionTypeName = request.inspectionTypeName,
            inspectionTypeUpdatedTime = request.inspectionTypeUpdatedTime,
            inspectionContentVersion = request.inspectionContentVersion,
            location = request.location,
            inspectionDateMillis = request.inspectionDateMillis,
            description = request.description,
            notes = request.notes,
            questionsJson = request.questionsJson,
            answersJson = request.answersJson,
            imagesJson = request.imagesJson,
            createdAt = request.createdAt,
            userId = request.userId.toLong()
        )
    }

    suspend fun getAllDrafts(userId: Int): List<InspectionDraft> = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getInspectionDraftsByUserId(userId.toLong()).executeAsList()
    }

    suspend fun getDraftById(id: Long): InspectionDraft? = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getInspectionDraftById(id).executeAsOneOrNull()
    }

    suspend fun deleteDraft(id: Long) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.deleteInspectionDraft(id)
    }
}
