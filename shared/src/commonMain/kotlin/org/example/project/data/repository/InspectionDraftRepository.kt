package org.example.project.data.repository

import org.example.project.data.model.CreateInspectionDraftRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.InspectionDraft

class InspectionDraftRepository(private val database: AppDatabase) {
    fun saveDraft(request: CreateInspectionDraftRequest) {
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

    fun getAllDrafts(userId: Int): List<InspectionDraft> {
        return database.appDatabaseQueries.getInspectionDraftsByUserId(userId.toLong()).executeAsList()
    }

    fun getDraftById(id: Long): InspectionDraft? {
        return database.appDatabaseQueries.getInspectionDraftById(id).executeAsOneOrNull()
    }

    fun deleteDraft(id: Long) {
        database.appDatabaseQueries.deleteInspectionDraft(id)
    }
}
