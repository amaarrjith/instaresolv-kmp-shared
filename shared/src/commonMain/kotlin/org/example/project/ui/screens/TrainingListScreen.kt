package org.example.project.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import org.example.project.colors.AppColors
import org.example.project.data.model.TrainingData
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.NavigationBackIcon
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingListScreen(
    onBackClicked: () -> Unit,
    onItemClicked: (Int) -> Unit
) {
    val viewModel: TrainingListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(
                    onBackClicked
                )
                Text(
                    text = stringResource(Res.string.myTraining).uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 22.dp)
                .background(Color.White)
        ) {
            if (uiState.isLoading && uiState.trainings.isEmpty()) {
                AppLoader()
            } else if (uiState.error != null && uiState.trainings.isEmpty()) {
                ErrorRetryView(
                    errorMessage = uiState.error ?: "",
                    onRetryClick = { viewModel.loadTrainings(isRefresh = true) }
                )
            } else {
                PullToRefreshBox(
                    isRefreshing = uiState.isLoading,
                    onRefresh = { viewModel.loadTrainings(isRefresh = true) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (uiState.trainings.isEmpty()) {
                        EmptyScreenView(
                            message = stringResource(Res.string.noTrainingsFound)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp)
                        ) {
                            items(uiState.trainings.size) { index ->
                                if (index >= uiState.trainings.size - 1 && !uiState.isLoading && !uiState.isPaginating && !uiState.isLastPage) {
                                    LaunchedEffect(key1 = index) {
                                        viewModel.loadTrainings(isRefresh = false)
                                    }
                                }
                                TrainingListItem(
                                    training = uiState.trainings[index],
                                    onClick = { onItemClicked(uiState.trainings[index].id) }
                                )
                                if (index < uiState.trainings.size - 1) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    HorizontalDivider(color = Color(0xFFF2F2F2))
                                }
                            }
                            if (uiState.isPaginating) {
                                item {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = AppColors.Primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrainingListItem(
    training: TrainingData,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Thumbnail Image with optional overlay
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 75.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            val thumbnail = training.thumbnailImage
            if (thumbnail.isNullOrBlank()) {
                // Mockup style placeholder (grey box with white circle + light-grey question mark)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFEEEEEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?",
                            style = textStyle(size = 18.sp, weight = FontWeight.Bold, color = Color.LightGray)
                        )
                    }
                }
            } else {
                WebImageView(
                    imageUrl = thumbnail,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Details column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Status and Code Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val (badgeText, badgeBg, badgeTextColor) = when (training.status) {
                    0 -> Triple("NOT STARTED", Color(0xFF2E6AC6), Color.White)
                    1 -> Triple("STARTED", Color(0xFFF57C00), Color.White)
                    2 -> Triple("FINISHED TRAINING", Color(0xFF00A82B), Color.White)
                    3 -> Triple("PASSED", Color(0xFF00A82B), Color.White)
                    else -> Triple("COMPLETED", Color(0xFF00A82B), Color.White)
                }

                Box(
                    modifier = Modifier
                        .background(badgeBg, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = textStyle(size = 9.sp, weight = FontWeight.Bold, color = badgeTextColor),
                        letterSpacing = 0.2.sp
                    )
                }

                if (!training.trainingCode.isNullOrBlank()) {
                    Text(
                        text = training.trainingCode,
                        style = textStyle(size = 11.sp, weight = FontWeight.SemiBold, color = AppColors.DarkGray)
                    )
                }
            }

            // Title
            Text(
                text = training.title,
                style = textStyle(size = 14.sp, weight = FontWeight.Bold, color = AppColors.Black),
                maxLines = 2,
                lineHeight = 18.sp
            )

            // Progress bar (only for In Progress / status = 1)
            var progressInt: Int = 0
            progressInt = when (training.status) {
                0 -> {
                    0
                }
                1 -> {
                    training.progress?.toInt() ?: 0
                }
                else -> {
                    100
                }
            }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "$progressInt%",
                        style = textStyle(size = 10.sp, weight = FontWeight.Medium, color = AppColors.DarkGray),
                        modifier = Modifier.align(Alignment.End)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color(0xFFE5E5E5), shape = RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressInt.toFloat() / 100f)
                                .height(4.dp)
                                .background(Color(0xFF2E6AC6), shape = RoundedCornerShape(2.dp))
                        )
                    }
                }


            // Chips spacing
            Spacer(modifier = Modifier.height(2.dp))

            // Action / Content Chips Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TrainingChip("Training Video")

                if (training.hasQuiz) {
                    TrainingChip("Quiz")
                }

                TrainingChip("Certificate")
            }
        }
    }
}

@Composable
fun TrainingChip(text: String) {
    Box(
        modifier = Modifier
            .border(BorderStroke(1.dp, Color(0xFFE5E5E5)), shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = textStyle(size = 10.sp, weight = FontWeight.Medium, color = AppColors.DarkGray)
        )
    }
}
