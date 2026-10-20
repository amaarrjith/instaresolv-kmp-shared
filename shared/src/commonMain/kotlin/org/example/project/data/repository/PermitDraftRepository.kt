package org.example.project.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.project.data.model.CreatePermitDraftRequest
import org.example.project.shared.db.AppDatabase
import org.example.project.shared.db.PermitDraft

class PermitDraftRepository(private val database: AppDatabase) {
    suspend fun saveDraft(request: CreatePermitDraftRequest) = withContext(Dispatchers.IO) {
        val permitTypeJson = request.permitType?.let { Json.encodeToString(it) }
        val projectJson = request.selectedProject?.let { Json.encodeToString(it) }
        val userJson = request.selectedUser?.let { Json.encodeToString(it) }
        val certificateValidityAnswersJson = Json.encodeToString(request.certificateValidityAnswers)
        val generalConditionAnswersJson = Json.encodeToString(request.generalConditionAnswers)
        val generalConditionRemarksJson = Json.encodeToString(request.generalConditionRemarks)

        if (request.draftId == null) {
            database.appDatabaseQueries.insertPermitDraft(
                permitTypeId = request.permitTypeId.toLong(),
                permitTypeJson = permitTypeJson,
                projectJson = projectJson,
                userJson = userJson,
                permitDateMillis = request.permitDateMillis,
                startTime = request.startTime,
                endTime = request.endTime,
                certificateValidityAnswersJson = certificateValidityAnswersJson,
                generalConditionAnswersJson = generalConditionAnswersJson,
                generalConditionRemarksJson = generalConditionRemarksJson,
                signatureUrl = request.signatureUrl,
                signatureDateMillis = request.signatureDateMillis,
                signatureTime = request.signatureTime,
                reportedBy = request.reportedBy,
                contractorName = request.contractorName,
                createdAt = request.createdAt,
                userId = request.userId.toLong()
            )
        } else {
            database.appDatabaseQueries.updatePermitDraft(
                permitTypeId = request.permitTypeId.toLong(),
                permitTypeJson = permitTypeJson,
                projectJson = projectJson,
                userJson = userJson,
                permitDateMillis = request.permitDateMillis,
                startTime = request.startTime,
                endTime = request.endTime,
                certificateValidityAnswersJson = certificateValidityAnswersJson,
                generalConditionAnswersJson = generalConditionAnswersJson,
                generalConditionRemarksJson = generalConditionRemarksJson,
                signatureUrl = request.signatureUrl,
                signatureDateMillis = request.signatureDateMillis,
                signatureTime = request.signatureTime,
                reportedBy = request.reportedBy,
                contractorName = request.contractorName,
                createdAt = request.createdAt,
                userId = request.userId.toLong(),
                id = request.draftId
            )
        }
    }

    suspend fun getAllDrafts(userId: Int): List<PermitDraft> = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getPermitDraftsByUserId(userId.toLong()).executeAsList()
    }

    suspend fun getDraftById(id: Long): PermitDraft? = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.getPermitDraftById(id).executeAsOneOrNull()
    }

    suspend fun deleteDraft(id: Long) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.deletePermitDraft(id)
    }

    suspend fun deleteDrafts(ids: List<Long>) = withContext(Dispatchers.IO) {
        database.appDatabaseQueries.transaction {
            ids.forEach { database.appDatabaseQueries.deletePermitDraft(it) }
        }
    }
}
