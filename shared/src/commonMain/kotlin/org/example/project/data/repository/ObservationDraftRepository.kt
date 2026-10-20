package org.example.project.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.data.model.CreateObservationDraftRequest
import org.example.project.data.model.CreateObservationRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.ObservationDraft

class ObservationDraftRepository(private val database: AppDatabase) {
    suspend fun saveDraft(request: CreateObservationDraftRequest) = withContext(Dispatchers.IO) {
        val imageDescriptions = request.imageDescription?.map { Json.encodeToString(it) } ?: emptyList()
        val notifications = request.notificationTo.map { Json.encodeToString(it) }

        if (request.draftId == null) {
            database.appDatabaseQueries.insertDraft(
                title = request.observationTitle ?: "",
                location = request.location ?: "",
                description = request.description ?: "",
                groupSpecified = request.groupSpecified.toLong(),
                groupJson = request.group?.let { Json.encodeToString(it) },
                reportedBy = request.reportedBy ?: "",
                customResponsiblePersonName = request.customResponsiblePerson?.name ?: "",
                responsiblePersonId = request.responsiblePerson?.userId?.toLong() ?: 0L,
                responsiblePersonName = request.responsiblePersonName ?: "",
                responsiblePersonEmail = request.responsiblePersonEmail ?: "",
                audioLink = request.audioLink,
                imageDescriptionsJson = imageDescriptions,
                notificationToJson = notifications,
                createdAt = request.createdAt,
                userId = request.userId.toLong()
            )
        } else {
            database.appDatabaseQueries.updateDraft(
                title = request.observationTitle ?: "",
                location = request.location ?: "",
                description = request.description ?: "",
                groupSpecified = request.groupSpecified.toLong(),
                groupJson = request.group?.let { Json.encodeToString(it) },
                reportedBy = request.reportedBy ?: "",
                customResponsiblePersonName = request.customResponsiblePerson?.name ?: "",
                responsiblePersonId = request.responsiblePerson?.userId?.toLong() ?: 0L,
                responsiblePersonName = request.responsiblePersonName ?: "",
                responsiblePersonEmail = request.responsiblePersonEmail ?: "",
                audioLink = request.audioLink,
                imageDescriptionsJson = imageDescriptions,
                notificationToJson = notifications,
                createdAt = request.createdAt,
                userId = request.userId.toLong(),
                id = request.draftId
            )
        }
    }

    suspend fun getAllDrafts(userId: Int): List<ObservationDraft> = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getDraftsByUserId(userId.toLong()).executeAsList()
    }

    suspend fun getDraftById(id: Long): ObservationDraft? = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getDraftById(id).executeAsOneOrNull()
    }

    suspend fun deleteDraft(id: Long) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.deleteDraft(id)
    }

    suspend fun deleteDrafts(ids: List<Long>) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.transaction {
            ids.forEach { database.appDatabaseQueries.deleteDraft(it) }
        }
    }
}
