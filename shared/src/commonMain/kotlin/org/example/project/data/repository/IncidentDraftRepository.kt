package org.example.project.data.repository

import org.example.project.data.model.CreateIncidentDraftRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.IncidentDraft

class IncidentDraftRepository(private val database: AppDatabase) {
    fun saveDraft(request: CreateIncidentDraftRequest) {
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

    fun getAllDrafts(userId: Int): List<IncidentDraft> {
        return database.appDatabaseQueries.getIncidentDraftsByUserId(userId.toLong()).executeAsList()
    }

    fun getDraftById(id: Long): IncidentDraft? {
        return database.appDatabaseQueries.getIncidentDraftById(id).executeAsOneOrNull()
    }

    fun deleteDraft(id: Long) {
        database.appDatabaseQueries.deleteIncidentDraft(id)
    }
}
