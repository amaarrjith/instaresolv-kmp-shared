package org.example.project.domain.repository

import org.example.project.data.model.CreateLessonLearnedRequest
import org.example.project.data.model.CreateLessonLearnedResponseData
import org.example.project.data.model.LessonLearnedData
import org.example.project.data.model.LessonLearnedDetailRequest
import org.example.project.data.model.LessonLearnedDetailResponseData
import org.example.project.data.model.LessonLearnedListRequest
import org.example.project.network.NetworkResult

interface LessonLearnedRepository {
    suspend fun createLessonLearned(request: CreateLessonLearnedRequest): NetworkResult<CreateLessonLearnedResponseData>
    suspend fun getLessonsLearnedList(request: LessonLearnedListRequest): NetworkResult<List<LessonLearnedData>>
    suspend fun getLessonLearnedDetail(request: LessonLearnedDetailRequest): NetworkResult<LessonLearnedDetailResponseData>
}
