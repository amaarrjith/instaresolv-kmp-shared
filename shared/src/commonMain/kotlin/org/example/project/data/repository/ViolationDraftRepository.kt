package org.example.project.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.data.model.CreateViolationDraftRequest
import org.example.project.data.model.LocalViolationImage
import org.example.project.data.model.Project
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.ViolationDraft

class ViolationDraftRepository(private val database: AppDatabase) {
    suspend fun saveDraft(request: CreateViolationDraftRequest) = withContext(Dispatchers.IO) {
        val imagesJson = request.images.map { Json.encodeToString(it) }

        if (request.draftId == null) {
            database.appDatabaseQueries.insertViolationDraft(
                facilitiesId = request.facilitiesId,
                facilityJson = request.facility?.let { Json.encodeToString(it) },
                employeeName = request.employeeName,
                employeeId = request.employeeId,
                violationDate = request.violationDate,
                violationDateMillis = request.violationDateMillis,
                location = request.location,
                description = request.description,
                imagesJson = imagesJson,
                reportedBy = request.reportedBy,
                createdAt = request.createdAt,
                userId = request.userId.toLong()
            )
        } else {
            database.appDatabaseQueries.updateViolationDraft(
                facilitiesId = request.facilitiesId,
                facilityJson = request.facility?.let { Json.encodeToString(it) },
                employeeName = request.employeeName,
                employeeId = request.employeeId,
                violationDate = request.violationDate,
                violationDateMillis = request.violationDateMillis,
                location = request.location,
                description = request.description,
                imagesJson = imagesJson,
                reportedBy = request.reportedBy,
                createdAt = request.createdAt,
                userId = request.userId.toLong(),
                id = request.draftId
            )
        }
    }

    suspend fun getAllDrafts(userId: Int): List<ViolationDraft> = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getViolationDraftsByUserId(userId.toLong()).executeAsList()
    }

    suspend fun getDraftById(id: Long): ViolationDraft? = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getViolationDraftById(id).executeAsOneOrNull()
    }

    suspend fun deleteDraft(id: Long) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.deleteViolationDraft(id)
    }
}
