package org.example.project.data.repository

import org.example.project.data.model.CreateToolBoxTalkDraftRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.ToolBoxTalkDraft

class ToolBoxTalkDraftRepository(private val database: AppDatabase) {
    fun saveDraft(request: CreateToolBoxTalkDraftRequest) {
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

    fun getAllDrafts(userId: Int): List<ToolBoxTalkDraft> {
        return database.appDatabaseQueries.getToolBoxTalkDraftsByUserId(userId.toLong()).executeAsList()
    }

    fun getDraftById(id: Long): ToolBoxTalkDraft? {
        return database.appDatabaseQueries.getToolBoxTalkDraftById(id).executeAsOneOrNull()
    }

    fun deleteDraft(id: Long) {
        database.appDatabaseQueries.deleteToolBoxTalkDraft(id)
    }
}
