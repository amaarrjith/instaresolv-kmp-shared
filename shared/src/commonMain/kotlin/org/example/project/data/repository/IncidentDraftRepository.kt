package org.example.project.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.example.project.data.model.CreateIncidentDraftRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.IncidentDraft

class IncidentDraftRepository(private val database: AppDatabase) {
    suspend fun saveDraft(request: CreateIncidentDraftRequest) = withContext(Dispatchers.IO) {
        val idValue = if (request.id == 0L) null else request.id
        database.appDatabaseQueries.insertIncidentDraft(
            id = idValue,
            facilitiesId = request.facilitiesId?.toLong(),
            projectJson = request.projectJson,
            reportedBy = request.reportedBy,
            incidentDateMillis = request.incidentDateMillis,
            incidentTime = request.incidentTime,
            incidentLocation = request.incidentLocation,
            incidentTypesJson = request.incidentTypesJson,
            hasInjuredPerson = when (request.hasInjuredPerson) {
                true -> 1L
                false -> 0L
                null -> null
            },
            injuredEmployeesJson = request.injuredEmployeesJson,
            description = request.description,
            corrections = request.corrections,
            imagesJson = request.imagesJson,
            createdAt = request.createdAt,
            userId = request.userId.toLong()
        )
    }

    suspend fun getAllDrafts(userId: Int): List<IncidentDraft> = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getIncidentDraftsByUserId(userId.toLong()).executeAsList()
    }

    suspend fun getDraftById(id: Long): IncidentDraft? = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getIncidentDraftById(id).executeAsOneOrNull()
    }

    suspend fun deleteDraft(id: Long) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.deleteIncidentDraft(id)
    }
}
