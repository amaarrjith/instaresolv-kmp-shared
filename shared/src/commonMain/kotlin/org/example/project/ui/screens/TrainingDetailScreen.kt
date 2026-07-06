package org.example.project.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_download
import instaresolv.shared.generated.resources.ic_play
import org.example.project.colors.AppColors
import org.example.project.data.model.TrainingDetailData
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingDetailScreen(
    trainingId: Int,
    onBackClicked: () -> Unit,
    onPlayVideoClicked: (Int) -> Unit,
    onStartQuizClicked: (Int) -> Unit
) {
    val viewModel: TrainingDetailViewModel = koinInject(
        parameters = { parametersOf(trainingId) }
    )
    val uiState by viewModel.uiState.collectAsState()
    val fileDownloader = org.example.project.utilites.rememberFileDownloader()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(onBackClicked)
                Text(
                    text = "VIDEO",
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
                .background(Color.White)
        ) {
            if (uiState.isLoading) {
                AppLoader()
            } else if (uiState.error != null) {
                ErrorRetryView(
                    errorMessage = uiState.error ?: "",
                    onRetryClick = { viewModel.loadTrainingDetail() }
                )
            } else {
                uiState.detailData?.let { data ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Top Video/Thumbnail Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .background(Color.Black)
                        ) {
                            if (!data.thumbnailImage.isNullOrBlank()) {
                                WebImageView(
                                    imageUrl = data.thumbnailImage,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            
                            // Play Overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.1f))
                                    .clickable { onPlayVideoClicked(data.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.ic_play),
                                    contentDescription = null
                                )
                            }
                        }

                        // Content Padding details
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 22.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Status and Code Row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val (badgeText, badgeBg, badgeTextColor) = when (data.status) {
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

                                if (!data.trainingCode.isNullOrBlank()) {
                                    Text(
                                        text = data.trainingCode,
                                        style = textStyle(size = 11.sp, weight = FontWeight.SemiBold, color = AppColors.DarkGray)
                                    )
                                }
                            }

                            // Title
                            Text(
                                text = data.title,
                                style = textStyle(size = 18.sp, weight = FontWeight.Bold, color = AppColors.Black),
                                lineHeight = 22.sp
                            )

                            // Description
                            if (!data.description.isNullOrBlank()) {
                                var isExpanded by remember { mutableStateOf(false) }
                                val isLongDescription = data.description.length > 150
                                val displayText = if (isLongDescription && !isExpanded) {
                                    "${data.description.take(150)}..."
                                } else {
                                    data.description
                                }

                                Column {
                                    Text(
                                        text = displayText,
                                        style = textStyle(size = 13.sp, weight = FontWeight.Normal, color = AppColors.DarkGray),
                                        lineHeight = 18.sp
                                    )
                                    if (isLongDescription) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isExpanded) "Read Less" else "Read More",
                                            style = textStyle(size = 13.sp, weight = FontWeight.Bold, color = Color(0xFFD42027)),
                                            modifier = Modifier.clickable { isExpanded = !isExpanded }
                                        )
                                    }
                                }
                            }

                            // Quiz Section Card
                            if (data.hasQuiz) {
                                QuizSectionCard(
                                    isQuizEnabled = data.isQuizEnabled,
                                    onStartQuiz = {
                                        onStartQuizClicked(trainingId)
                                    }
                                )
                            }

                            // Download Lesson Material Card
                            if (!data.lessonMaterialUrl.isNullOrBlank()) {
                                DownloadMaterialCard(
                                    url = data.lessonMaterialUrl,
                                    onDownload = { materialUrl ->
                                        try {
                                            val fileName = "Lesson_Material_${Clock.System.now().toEpochMilliseconds()}.pdf"
                                            fileDownloader.downloadFile(materialUrl, fileName)
                                        } catch (e: Exception) {
                                            // Handle error
                                        }
                                    }
                                )
                            }

                            // Training Progress Row
                            data.trainingProgress?.let { progress ->
                                TrainingProgressSection(
                                    videoStatus = progress.video,
                                    quizStatus = progress.quiz,
                                    certificateUrl = data.certificateUrl,
                                    onDownloadCertificate = { certUrl ->
                                        try {
                                            val fileName = "Certificate_${Clock.System.now().toEpochMilliseconds()}.pdf"
                                            fileDownloader.downloadFile(certUrl, fileName)
                                        } catch (e: Exception) {
                                            // Handle error
                                        }
                                    }
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
fun QuizSectionCard(
    isQuizEnabled: Boolean,
    onStartQuiz: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FA), shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE9ECEF), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Quiz Section",
                        style = textStyle(size = 15.sp, weight = FontWeight.Bold, color = AppColors.Black)
                    )
                    Text(
                        text = if (isQuizEnabled) "Quiz section is unlocked" else "Watch Full video to unlock quiz section",
                        style = textStyle(size = 12.sp, weight = FontWeight.Normal, color = AppColors.DarkGray)
                    )
                }

                // Question mark in red circle icon
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFFD42027), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "?",
                        style = textStyle(size = 16.sp, weight = FontWeight.Bold, color = Color.White)
                    )
                }
            }

            // Start Quiz Button
            Button(
                onClick = { if (isQuizEnabled) onStartQuiz() },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isQuizEnabled) Color(0xFFD42027) else Color(0xFFCCCCCC),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Start Quiz",
                    style = textStyle(size = 14.sp, weight = FontWeight.Bold, color = Color.White)
                )
            }
        }
    }
}

@Composable
fun DownloadMaterialCard(
    url: String,
    onDownload: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE5E5E5), shape = RoundedCornerShape(8.dp))
            .clickable { onDownload(url) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // PDF Icon Box
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFFDE8E9), shape = RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PDF",
                style = textStyle(size = 11.sp, weight = FontWeight.Bold, color = Color(0xFFD42027))
            )
        }

        // Labels
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "Download Lesson Material",
                style = textStyle(size = 13.sp, weight = FontWeight.Bold, color = AppColors.Black)
            )
            Text(
                text = "PDF notes and references",
                style = textStyle(size = 11.sp, weight = FontWeight.Normal, color = AppColors.DarkGray)
            )
        }

        // Download Action Icon
        Image(
            painter = painterResource(Res.drawable.ic_download),
            contentDescription = "Download",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun TrainingProgressSection(
    videoStatus: Int,
    quizStatus: Int,
    certificateUrl: String?,
    onDownloadCertificate: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Training Progress",
            style = textStyle(size = 12.sp, weight = FontWeight.Medium, color = AppColors.DarkGray)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Video Progress Column
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Video",
                    style = textStyle(size = 13.sp, weight = FontWeight.Bold, color = AppColors.Black)
                )
                when (videoStatus) {
                    2 -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Canvas(modifier = Modifier.size(14.dp)) {
                                drawCircle(color = Color(0xFF00A82B))
                                val path = Path().apply {
                                    moveTo(size.width * 0.25f, size.height * 0.5f)
                                    lineTo(size.width * 0.45f, size.height * 0.7f)
                                    lineTo(size.width * 0.75f, size.height * 0.3f)
                                }
                                drawPath(path, color = Color.White, style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            }
                            Text("Finished", style = textStyle(size = 12.sp, weight = FontWeight.Medium, color = Color(0xFF00A82B)))
                        }
                    }
                    1 -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFFF57C00), CircleShape))
                            Text("In Progress", style = textStyle(size = 12.sp, weight = FontWeight.Medium, color = Color(0xFFF57C00)))
                        }
                    }
                    else -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).background(Color.Gray, CircleShape))
                            Text("Not Started", style = textStyle(size = 12.sp, weight = FontWeight.Medium, color = Color.Gray))
                        }
                    }
                }
            }

            // Quiz Progress Column
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Quiz",
                    style = textStyle(size = 13.sp, weight = FontWeight.Bold, color = AppColors.Black)
                )
                when (quizStatus) {
                    1 -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Canvas(modifier = Modifier.size(14.dp)) {
                                drawCircle(color = Color(0xFF00A82B))
                                val path = Path().apply {
                                    moveTo(size.width * 0.25f, size.height * 0.5f)
                                    lineTo(size.width * 0.45f, size.height * 0.7f)
                                    lineTo(size.width * 0.75f, size.height * 0.3f)
                                }
                                drawPath(path, color = Color.White, style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                            }
                            Text("Passed", style = textStyle(size = 12.sp, weight = FontWeight.Medium, color = Color(0xFF00A82B)))
                        }
                    }
                    2 -> {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Canvas(modifier = Modifier.size(14.dp)) {
                                drawCircle(color = Color(0xFFD42027))
                                drawLine(color = Color.White, start = Offset(size.width * 0.3f, size.height * 0.3f), end = Offset(size.width * 0.7f, size.height * 0.7f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
                                drawLine(color = Color.White, start = Offset(size.width * 0.7f, size.height * 0.3f), end = Offset(size.width * 0.3f, size.height * 0.7f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
                            }
                            Text("Failed", style = textStyle(size = 12.sp, weight = FontWeight.Medium, color = Color(0xFFD42027)))
                        }
                    }
                    else -> {
                        Text("NA", style = textStyle(size = 12.sp, weight = FontWeight.Medium, color = Color.Gray))
                    }
                }
            }

            // Certificate Progress Column
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Certificate",
                    style = textStyle(size = 13.sp, weight = FontWeight.Bold, color = AppColors.Black)
                )
                if (!certificateUrl.isNullOrBlank()) {
                    Text(
                        text = "Download",
                        style = textStyle(size = 12.sp, weight = FontWeight.Bold, color = Color(0xFF2E6AC6)),
                        modifier = Modifier.clickable { onDownloadCertificate(certificateUrl) }
                    )
                } else {
                    Text(
                        text = "NA",
                        style = textStyle(size = 12.sp, weight = FontWeight.Medium, color = Color.Gray)
                    )
                }
            }
        }
    }
}
