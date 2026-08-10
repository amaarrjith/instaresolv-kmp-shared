package org.example.project.domain.repository

import org.example.project.data.model.TrainingListRequest
import org.example.project.data.model.TrainingListResponse
import org.example.project.data.model.CommonResponse
import org.example.project.network.NetworkResult

interface TrainingRepository {
    suspend fun getMyTrainingList(
        request: TrainingListRequest
    ): NetworkResult<TrainingListResponse>

    suspend fun getAssignedTrainings(
        request: org.example.project.data.model.AssignedTrainingRequest
    ): NetworkResult<org.example.project.data.model.AssignedTrainingResponse>

    suspend fun getAllTrainings(
        request: org.example.project.data.model.AllTrainingRequest
    ): NetworkResult<org.example.project.data.model.AllTrainingResponse>

    suspend fun assignTraining(
        request: org.example.project.data.model.AssignTrainingRequest
    ): NetworkResult<org.example.project.data.model.AssignTrainingResponse>

    suspend fun getTrainingDetail(
        request: org.example.project.data.model.TrainingDetailRequest
    ): NetworkResult<org.example.project.data.model.TrainingDetailData>

    suspend fun getTrainingVideoUrl(
        request: org.example.project.data.model.TrainingVideoUrlRequest
    ): NetworkResult<org.example.project.data.model.TrainingVideoUrlData>

    suspend fun updateVideoProgress(
        request: org.example.project.data.model.TrainingVideoProgressRequest
    ): NetworkResult<org.example.project.data.model.TrainingVideoProgressResponse>

    suspend fun getQuizQuestions(
        request: org.example.project.data.model.QuizQuestionsRequest
    ): NetworkResult<org.example.project.data.model.QuizQuestionsResponse>

    suspend fun submitQuiz(
        request: org.example.project.data.model.QuizSubmitRequest
    ): NetworkResult<org.example.project.data.model.QuizSubmitResponse>
}
