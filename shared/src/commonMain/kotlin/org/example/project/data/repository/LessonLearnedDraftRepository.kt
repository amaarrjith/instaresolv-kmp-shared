package org.example.project.data.repository

import org.example.project.data.model.CreateLessonLearnedDraftRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.LessonLearnedDraft

class LessonLearnedDraftRepository(private val database: AppDatabase) {
    fun saveDraft(request: CreateLessonLearnedDraftRequest) {
        val idValue = if (request.id == 0L) null else request.id
        database.appDatabaseQueries.insertLessonLearnedDraft(
            id = idValue,
            facilitiesId = request.facilitiesId?.toLong(),
            projectJson = request.projectJson,
            title = request.title,
            description = request.description,
            reportedBy = request.reportedBy,
            imagesJson = request.imagesJson,
            createdAt = request.createdAt,
            userId = request.userId.toLong()
        )
    }

    fun getAllDrafts(userId: Int): List<LessonLearnedDraft> {
        return database.appDatabaseQueries.getLessonLearnedDraftsByUserId(userId.toLong()).executeAsList()
    }

    fun getDraftById(id: Long): LessonLearnedDraft? {
        return database.appDatabaseQueries.getLessonLearnedDraftById(id).executeAsOneOrNull()
    }

    fun deleteDraft(id: Long) {
        database.appDatabaseQueries.deleteLessonLearnedDraft(id)
    }
}
