package org.example.project.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.example.project.data.model.CreateToolBoxTalkDraftRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.ToolBoxTalkDraft

class ToolBoxTalkDraftRepository(private val database: AppDatabase) {
    suspend fun saveDraft(request: CreateToolBoxTalkDraftRequest) = withContext(Dispatchers.IO) {
        val idValue = if (request.id == 0L) null else request.id
        database.appDatabaseQueries.insertToolBoxTalkDraft(
            id = idValue,
            facilitiesId = request.facilitiesId?.toLong(),
            projectJson = request.projectJson,
            dateMillis = request.dateMillis,
            startTime = request.startTime,
            endTime = request.endTime,
            topic = request.topic,
            discussionPointsJson = request.discussionPointsJson,
            attendeesJson = request.attendeesJson,
            imagesJson = request.imagesJson,
            reportedBy = request.reportedBy,
            createdAt = request.createdAt,
            userId = request.userId.toLong()
        )
    }

    suspend fun getAllDrafts(userId: Int): List<ToolBoxTalkDraft> = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getToolBoxTalkDraftsByUserId(userId.toLong()).executeAsList()
    }

    suspend fun getDraftById(id: Long): ToolBoxTalkDraft? = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getToolBoxTalkDraftById(id).executeAsOneOrNull()
    }

    suspend fun deleteDraft(id: Long) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.deleteToolBoxTalkDraft(id)
    }

    suspend fun deleteDrafts(ids: List<Long>) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.transaction {
            ids.forEach { database.appDatabaseQueries.deleteToolBoxTalkDraft(it) }
        }
    }
}
