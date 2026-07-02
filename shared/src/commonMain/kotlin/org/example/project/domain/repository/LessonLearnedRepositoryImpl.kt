package org.example.project.domain.repository

import org.example.project.data.model.CreateLessonLearnedRequest
import org.example.project.data.model.CreateLessonLearnedResponseData
import org.example.project.data.model.LessonLearnedData
import org.example.project.data.model.LessonLearnedDetailRequest
import org.example.project.data.model.LessonLearnedDetailResponseData
import org.example.project.data.model.LessonLearnedListRequest
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult

class LessonLearnedRepositoryImpl(
    private val apiService: AuthApiService
) : LessonLearnedRepository {

    override suspend fun createLessonLearned(request: CreateLessonLearnedRequest): NetworkResult<CreateLessonLearnedResponseData> {
        return apiService.createLessonLearned(request)
    }

    override suspend fun getLessonsLearnedList(request: LessonLearnedListRequest): NetworkResult<List<LessonLearnedData>> {
        return apiService.getLessonsLearnedList(request)
    }

    override suspend fun getLessonLearnedDetail(request: LessonLearnedDetailRequest): NetworkResult<LessonLearnedDetailResponseData> {
        return apiService.getLessonLearnedDetail(request)
    }
}
