package org.example.project.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.data.model.QuizQuestion
import org.example.project.data.model.QuizQuestionsRequest
import org.example.project.data.model.QuizSubmitRequest
import org.example.project.data.model.QuizSubmitResponse
import org.example.project.data.model.SubmittedAnswer
import org.example.project.domain.repository.TrainingRepository
import org.example.project.network.NetworkResult

data class QuizUiState(
    val isLoading: Boolean = false,
    val isRtl: Boolean = false,
    val totalQuestionsCount: Int = 0,
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(), // questionId -> optionId
    val isSubmitting: Boolean = false,
    val quizResult: QuizSubmitResponse? = null,
    val error: String? = null
)

class QuizViewModel(
    private val repository: TrainingRepository,
    private val trainingId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadQuizQuestions()
    }

    fun loadQuizQuestions() {
        _uiState.update { it.copy(isLoading = true, error = null, quizResult = null) }
        viewModelScope.launch {
            when (val result = repository.getQuizQuestions(QuizQuestionsRequest(id = trainingId))) {
                is NetworkResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isRtl = result.data?.isRtl ?: false,
                            totalQuestionsCount = result.data?.totalQuestionsCount ?: 0,
                            questions = result.data?.questions ?: emptyList(),
                            currentQuestionIndex = 0,
                            selectedAnswers = emptyMap()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun selectOption(questionId: Int, optionId: Int) {
        _uiState.update { state ->
            val updatedAnswers = state.selectedAnswers.toMutableMap()
            updatedAnswers[questionId] = optionId
            state.copy(selectedAnswers = updatedAnswers)
        }
    }

    fun goToNextQuestion() {
        _uiState.update { state ->
            if (state.currentQuestionIndex < state.questions.size - 1) {
                state.copy(currentQuestionIndex = state.currentQuestionIndex + 1)
            } else {
                state
            }
        }
    }

    fun goToPreviousQuestion() {
        _uiState.update { state ->
            if (state.currentQuestionIndex > 0) {
                state.copy(currentQuestionIndex = state.currentQuestionIndex - 1)
            } else {
                state
            }
        }
    }

    fun submitQuiz() {
        val state = _uiState.value
        val answersList = state.questions.map { question ->
            val selectedOptionId = state.selectedAnswers[question.id] ?: -1
            SubmittedAnswer(
                questionId = question.id,
                selectedAnswerId = selectedOptionId
            )
        }

        _uiState.update { it.copy(isSubmitting = true, error = null) }
        viewModelScope.launch {
            val request = QuizSubmitRequest(
                id = trainingId,
                submittedAnswers = answersList
            )
            when (val result = repository.submitQuiz(request)) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, quizResult = result.data) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isSubmitting = false, error = result.message) }
                }
            }
        }
    }
}
