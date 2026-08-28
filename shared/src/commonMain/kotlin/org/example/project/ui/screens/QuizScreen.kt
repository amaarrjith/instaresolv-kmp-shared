package org.example.project.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.NavigationBackIcon
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    trainingId: Int,
    onBackClicked: () -> Unit
) {
    val viewModel: QuizViewModel = koinInject(
        parameters = { parametersOf(trainingId) }
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            if (uiState.quizResult == null) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(vertical = 10.dp)
                        .padding(end = 22.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationBackIcon(onBackClicked)
                    Text(
                        text = stringResource(Res.string.quiz1),
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Bold
                        ),
                        color = AppColors.Black
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            if (uiState.isLoading) {
                AppLoader()
            } else if (uiState.error != null && uiState.quizResult == null) {
                ErrorRetryView(
                    errorMessage = uiState.error ?: "",
                    modifier = Modifier.fillMaxSize(),
                    onRetryClick = { viewModel.loadQuizQuestions() }
                )
            } else if (uiState.quizResult != null) {
                // Show Quiz Result Screen
                QuizResultView(
                    result = uiState.quizResult!!,
                    onDoneClicked = onBackClicked,
                    onRetryClicked = { viewModel.loadQuizQuestions() }
                )
            } else if (uiState.questions.isNotEmpty()) {
                val currentQuestion = uiState.questions[uiState.currentQuestionIndex]
                val selectedOptionId = uiState.selectedAnswers[currentQuestion.id]

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Question Indicator text
                        Text(
                            text = "Question ${uiState.currentQuestionIndex + 1} of ${uiState.questions.size}",
                            style = textStyle(size = 12.sp, weight = FontWeight.Medium, color = AppColors.TextGray)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Segmented Progress indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (i in 0 until uiState.questions.size) {
                                val segmentColor = if (i <= uiState.currentQuestionIndex) {
                                    Color(0xFFD42027)
                                } else {
                                    Color(0xFFEEEEEE)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .background(segmentColor, shape = RoundedCornerShape(2.dp))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(28.dp))

                        // Question Index Header
                        Text(
                            text = "QUESTION : ${"0${uiState.currentQuestionIndex + 1}".takeLast(2)}",
                            style = textStyle(size = 11.sp, weight = FontWeight.Bold, color = Color(0xFF2E6AC6)),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Question Text
                        Text(
                            text = currentQuestion.question,
                            style = textStyle(size = 18.sp, weight = FontWeight.Bold, color = AppColors.Black),
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Options Card List
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            currentQuestion.options.forEach { option ->
                                val isSelected = selectedOptionId == option.id
                                OptionRowCard(
                                    optionText = option.title,
                                    isSelected = isSelected,
                                    onClick = {
                                        viewModel.selectOption(currentQuestion.id, option.id)
                                    }
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        val isLastQuestion = uiState.currentQuestionIndex == uiState.questions.size - 1
                        val nextButtonText = if (isLastQuestion) "Submit" else "Next"
                        val isOptionSelected = selectedOptionId != null

                        Button(
                            onClick = {
                                if (isLastQuestion) {
                                    viewModel.submitQuiz()
                                } else {
                                    viewModel.goToNextQuestion()
                                }
                            },
                            enabled = isOptionSelected && !uiState.isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD42027),
                                disabledContainerColor = Color(0xFFE0E0E0)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (uiState.isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = nextButtonText,
                                    style = textStyle(size = 15.sp, weight = FontWeight.Bold, color = Color.White)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionRowCard(
    optionText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cardBg = if (isSelected) Color(0xFFFEECEE) else Color(0xFFF7F7F7)
    val cardBorder = if (isSelected) Color(0xFFD42027) else Color(0xFFEEEEEE)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardBg, shape = RoundedCornerShape(8.dp))
            .border(1.dp, cardBorder, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color(0xFFD42027), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(10.dp)) {
                    val path = Path().apply {
                        moveTo(size.width * 0.15f, size.height * 0.5f)
                        lineTo(size.width * 0.42f, size.height * 0.77f)
                        lineTo(size.width * 0.85f, size.height * 0.2f)
                    }
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(1.5.dp, Color(0xFF9E9E9E), CircleShape)
                    .background(Color.Transparent, shape = CircleShape)
            )
        }

        Text(
            text = optionText,
            style = textStyle(size = 14.sp, weight = FontWeight.Medium, color = AppColors.Black),
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuizResultView(
    result: org.example.project.data.model.QuizSubmitResponse,
    onDoneClicked: () -> Unit,
    onRetryClicked: () -> Unit
) {
    val isPassed = result.status == 2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 40.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            if (isPassed) {
                // Success Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF00A82B), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(36.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.15f, size.height * 0.5f)
                            lineTo(size.width * 0.42f, size.height * 0.77f)
                            lineTo(size.width * 0.85f, size.height * 0.2f)
                        }
                        drawPath(
                            path = path,
                            color = Color.White,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource(Res.string.completedSuccessfully),
                    style = textStyle(size = 20.sp, weight = FontWeight.Bold, color = Color(0xFF00A82B)),
                    textAlign = TextAlign.Center
                )
            } else {
                // Failure Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFD42027), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(32.dp)) {
                        drawLine(
                            color = Color.White,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.White,
                            start = Offset(size.width, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource(Res.string.quizFailed),
                    style = textStyle(size = 20.sp, weight = FontWeight.Bold, color = Color(0xFFD42027)),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Status message
            Text(
                text = result.statusMessage,
                style = textStyle(size = 14.sp, weight = FontWeight.Normal, color = AppColors.TextGray),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(36.dp))

            // Score Box Card
            Box(
                modifier = Modifier
                    .background(Color(0xFFF7F7F7), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 48.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Score : ${result.correctAnswersCount}/${result.totalQuestionsCount}",
                    style = textStyle(size = 18.sp, weight = FontWeight.Bold, color = AppColors.Black)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onDoneClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD42027)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.done),
                    style = textStyle(size = 15.sp, weight = FontWeight.Bold, color = Color.White)
                )
            }

            if (!isPassed) {
                Text(
                    text = stringResource(Res.string.retryQuiz),
                    style = textStyle(size = 14.sp, weight = FontWeight.Bold, color = Color(0xFFD42027)),
                    modifier = Modifier.clickable { onRetryClicked() }
                )
            }
        }
    }
}
