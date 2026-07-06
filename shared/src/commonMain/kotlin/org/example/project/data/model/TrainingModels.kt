package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TrainingListRequest(
    val pageNumber: Int,
    val limit: Int
)

@Serializable
data class TrainingData(
    val id: Int,
    val title: String,
    val status: Int,
    val progress: Double? = null,
    val thumbnailImage: String? = null,
    val trainingCode: String? = null,
    val hasQuiz: Boolean = false
)

@Serializable
data class TrainingListResponse(
    val trainings: List<TrainingData> = emptyList()
)

@Serializable
data class TrainingDetailRequest(
    val id: Int
)

@Serializable
data class TrainingProgress(
    val video: Int,
    val quiz: Int
)

@Serializable
data class TrainingDetailData(
    val id: Int,
    val title: String,
    val description: String? = null,
    val status: Int,
    val thumbnailImage: String? = null,
    val trainingCode: String? = null,
    val hasQuiz: Boolean = false,
    val isQuizEnabled: Boolean = false,
    val certificateUrl: String? = null,
    val lessonMaterialUrl: String? = null,
    val trainingProgress: TrainingProgress? = null
)

@Serializable
data class TrainingVideoUrlRequest(
    val id: Int
)

@Serializable
data class TrainingVideoUrlData(
    val videoUrl: String,
    val status: Int,
    val lastPlayBackTime: Int? = null
)

@Serializable
data class QuizQuestionsRequest(
    val id: Int
)

@Serializable
data class QuizOption(
    val id: Int,
    val title: String
)

@Serializable
data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<QuizOption> = emptyList()
)

@Serializable
data class QuizQuestionsResponse(
    val isRtl: Boolean = false,
    val totalQuestionsCount: Int,
    val questions: List<QuizQuestion> = emptyList()
)

@Serializable
data class SubmittedAnswer(
    val questionId: Int,
    val selectedAnswerId: Int
)

@Serializable
data class QuizSubmitRequest(
    val id: Int,
    val submittedAnswers: List<SubmittedAnswer>
)

@Serializable
data class QuizSubmitResponse(
    val totalQuestionsCount: Int,
    val score: Int,
    val correctAnswersCount: Int,
    val status: Int,
    val statusMessage: String
)
