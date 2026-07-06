package org.example.project.domain.repository

import org.example.project.data.model.TrainingListRequest
import org.example.project.data.model.TrainingListResponse
import org.example.project.data.model.CommonResponse
import org.example.project.data.remote.api.AuthApiService
import org.example.project.network.NetworkResult

class TrainingRepositoryImpl(
    private val apiService: AuthApiService
) : TrainingRepository {
    override suspend fun getMyTrainingList(
        request: TrainingListRequest
    ): NetworkResult<TrainingListResponse> {
        return apiService.getMyTrainingList(request)
    }

    override suspend fun getTrainingDetail(
        request: org.example.project.data.model.TrainingDetailRequest
    ): NetworkResult<org.example.project.data.model.TrainingDetailData> {
        return apiService.getTrainingDetail(request)
    }

    override suspend fun getTrainingVideoUrl(
        request: org.example.project.data.model.TrainingVideoUrlRequest
    ): NetworkResult<org.example.project.data.model.TrainingVideoUrlData> {
        return apiService.getTrainingVideoUrl(request)
    }

    override suspend fun getQuizQuestions(
        request: org.example.project.data.model.QuizQuestionsRequest
    ): NetworkResult<org.example.project.data.model.QuizQuestionsResponse> {
        return apiService.getQuizQuestions(request)
    }

    override suspend fun submitQuiz(
        request: org.example.project.data.model.QuizSubmitRequest
    ): NetworkResult<org.example.project.data.model.QuizSubmitResponse> {
        return apiService.submitQuiz(request)
    }
}
