package org.example.project.data.repository

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.data.model.CreateViolationDraftRequest
import org.example.project.data.model.LocalViolationImage
import org.example.project.data.model.Project
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.ViolationDraft

class ViolationDraftRepository(private val database: AppDatabase) {
    fun saveDraft(request: CreateViolationDraftRequest) {
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

    fun getAllDrafts(userId: Int): List<ViolationDraft> {
        return database.appDatabaseQueries.getViolationDraftsByUserId(userId.toLong()).executeAsList()
    }

    fun getDraftById(id: Long): ViolationDraft? {
        return database.appDatabaseQueries.getAllViolationDrafts().executeAsList().find { it.id == id }
    }

    fun deleteDraft(id: Long) {
        database.appDatabaseQueries.deleteViolationDraft(id)
    }
}
