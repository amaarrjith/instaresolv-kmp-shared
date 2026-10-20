package org.example.project.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.example.project.data.model.CreateLessonLearnedDraftRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.LessonLearnedDraft

class LessonLearnedDraftRepository(private val database: AppDatabase) {
    suspend fun saveDraft(request: CreateLessonLearnedDraftRequest) = withContext(Dispatchers.IO) {
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

    suspend fun getAllDrafts(userId: Int): List<LessonLearnedDraft> = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getLessonLearnedDraftsByUserId(userId.toLong()).executeAsList()
    }

    suspend fun getDraftById(id: Long): LessonLearnedDraft? = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getLessonLearnedDraftById(id).executeAsOneOrNull()
    }

    suspend fun deleteDraft(id: Long) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.deleteLessonLearnedDraft(id)
    }

    suspend fun deleteDrafts(ids: List<Long>) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.transaction {
            ids.forEach { database.appDatabaseQueries.deleteLessonLearnedDraft(it) }
        }
    }
}
