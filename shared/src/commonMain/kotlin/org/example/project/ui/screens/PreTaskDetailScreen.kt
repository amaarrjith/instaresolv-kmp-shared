package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_translate
import instaresolv.shared.generated.resources.ic_translate_done
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import instaresolv.shared.generated.resources.ic_avatar
import instaresolv.shared.generated.resources.ic_share
import org.example.project.colors.AppColors
import org.example.project.data.model.PreTaskDetailResponseData
import org.example.project.data.settings.formatDate
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.UploadedImagesSection
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.ErrorRetryView
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*
import org.example.project.utilites.ToastHost

@Composable
fun PreTaskDetailScreen(
    preTaskId: Int,
    onBackClicked: () -> Unit
) {
    val viewModel: PreTaskDetailViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    
    var previewImageUrl by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }
    var isTranslationDone by remember { mutableStateOf(false) }
    val noTranslationText = stringResource(Res.string.noTranslationInfoAvailable)

    LaunchedEffect(preTaskId) {
        viewModel.loadPreTaskDetail(preTaskId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (uiState is PreTaskDetailUiState.Success) {
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 22.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                            AppBorderButton(
                                title = stringResource(Res.string.generatePdf),
                                onClick = {

                                },
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                modifier = Modifier.weight(1f)
                                    .clickable { }
                                    .height(48.dp)
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Share", style = textStyle(size = 14.sp, weight = FontWeight.Bold), color = AppColors.Black)
                                Spacer(Modifier.width(8.dp))
                                Image(
                                    painter = painterResource(Res.drawable.ic_share),
                                    contentDescription = null,
                                )
                            }

                    }
                }
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is PreTaskDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppLoader()
                }
            }
            is PreTaskDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ErrorRetryView(
                        errorMessage = state.message,
                        onRetryClick = { viewModel.loadPreTaskDetail(preTaskId) }
                    )
                }
            }
            is PreTaskDetailUiState.Success -> {
                val data = state.data
                Box() {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(paddingValues)
                            .padding(top = 40.dp)
                    ) {
                        // Header Handle
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .background(Color.LightGray, RoundedCornerShape(2.dp))
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isTranslationDone) AppColors.Primary else Color(
                                                0xFF8F9098
                                            )
                                        )
                                        .clickable {
                                            val hasTranslation =
                                                !data.translatedNotes.isNullOrBlank() ||
                                                        data.images?.any { !it.translatedImageDescription.isNullOrBlank() } == true
                                            if (hasTranslation) {
                                                isTranslationDone = !isTranslationDone
                                            } else {
                                                infoMessage = noTranslationText
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(if (isTranslationDone) Res.drawable.ic_translate_done else Res.drawable.ic_translate),
                                        contentDescription = null,
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                        // Task Title
                        Column(modifier = Modifier.padding(horizontal = 22.dp)) {
                            Text(
                                text = stringResource(Res.string.taskTitle),
                                style = textStyle(12.sp, FontWeight.Normal),
                                color = AppColors.TextGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = data.taskTitle ?: "N/A",
                                style = textStyle(16.sp, FontWeight.Bold),
                                color = AppColors.Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Facility / Project
                            Text(
                                text = stringResource(Res.string.facilityProject),
                                style = textStyle(12.sp, FontWeight.Normal),
                                color = AppColors.TextGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                WebImageView(
                                    imageUrl = data.facilities?.groupImage ?: "",
                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = data.facilities?.groupName ?: "N/A",
                                        style = textStyle(14.sp, FontWeight.SemiBold),
                                        color = AppColors.Black
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                AppColors.TextGray.copy(alpha = 0.5f),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = data.facilities?.groupCode ?: "",
                                            style = textStyle(10.sp, FontWeight.Medium),
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Color(0xFFF0F0F0)
                            )

                            // Reported By
                            Text(
                                text = stringResource(Res.string.reportedBy),
                                style = textStyle(12.sp, FontWeight.Normal),
                                color = AppColors.TextGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_avatar),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Unspecified
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = data.reportedBy ?: "N/A",
                                    style = textStyle(14.sp, FontWeight.SemiBold),
                                    color = AppColors.Black
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Employee Name
                            Text(
                                text = stringResource(Res.string.employeeName),
                                style = textStyle(12.sp, FontWeight.Normal),
                                color = AppColors.TextGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = data.reportedBy
                                    ?: "N/A", // The design shows an employee name but API response doesn't explicitly have it besides reportedBy.
                                style = textStyle(14.sp, FontWeight.SemiBold),
                                color = AppColors.Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Date
                            Text(
                                text = stringResource(Res.string.date),
                                style = textStyle(12.sp, FontWeight.Normal),
                                color = AppColors.TextGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = data.date?.let {
                                    formatDate(
                                        it,
                                        "dd-MM-yyyy",
                                        "dd MMM yyyy"
                                    )
                                } ?: "N/A",
                                style = textStyle(14.sp, FontWeight.SemiBold),
                                color = AppColors.Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Start & End Time
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(Res.string.startTime),
                                        style = textStyle(12.sp, FontWeight.Normal),
                                        color = AppColors.TextGray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = data.startTime ?: "N/A",
                                        style = textStyle(14.sp, FontWeight.SemiBold),
                                        color = AppColors.Black
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(Res.string.endTime),
                                        style = textStyle(12.sp, FontWeight.Normal),
                                        color = AppColors.TextGray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = data.endTime ?: "N/A",
                                        style = textStyle(14.sp, FontWeight.SemiBold),
                                        color = AppColors.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // MSRA Reference
                            Text(
                                text = stringResource(Res.string.msraReference),
                                style = textStyle(12.sp, FontWeight.Normal),
                                color = AppColors.TextGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = data.msraReference.takeUnless { it.isNullOrEmpty() } ?: "-",
                                style = textStyle(14.sp, FontWeight.SemiBold),
                                color = AppColors.Black
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Permit Reference
                            Text(
                                text = stringResource(Res.string.permitReference),
                                style = textStyle(12.sp, FontWeight.Normal),
                                color = AppColors.TextGray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = data.permitReference.takeUnless { it.isNullOrEmpty() }
                                    ?: "-",
                                style = textStyle(14.sp, FontWeight.SemiBold),
                                color = AppColors.Black
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = Color(0xFFF0F0F0)
                            )

                            // Topics of Discussion
                            Text(
                                text = stringResource(Res.string.topicsOfDiscussion),
                                style = textStyle(14.sp, FontWeight.Bold),
                                color = AppColors.Primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            data.contents?.forEach { content ->
                                Text(
                                    text = content.title?.uppercase() ?: "",
                                    style = textStyle(12.sp, FontWeight.Bold),
                                    color = AppColors.BlackText
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val contentQuestions =
                                    data.questions?.filter { it.contentId == content.id }
                                        ?: emptyList()
                                contentQuestions.forEach { question ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = question.title ?: "",
                                            style = textStyle(12.sp, FontWeight.Medium),
                                            color = AppColors.Black,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        AnswerBadge(question.selectedAnswer ?: 0)
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color(0xFFF0F0F0),
                                    thickness = 0.5.dp
                                )
                            }

                            if (!data.otherTopic.isNullOrEmpty()) {
                                Text(
                                    text = stringResource(Res.string.others),
                                    style = textStyle(12.sp, FontWeight.Bold),
                                    color = AppColors.BlackText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                data.otherTopic.forEach { otherTopic ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = otherTopic.title ?: "",
                                            style = textStyle(12.sp, FontWeight.Medium),
                                            color = AppColors.Black,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        AnswerBadge(otherTopic.selectedAnswer ?: 0)
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = Color(0xFFF0F0F0),
                                    thickness = 0.5.dp
                                )
                            }

                            // AI Translated Notes
                            if (!data.notes.isNullOrBlank() || !data.translatedNotes.isNullOrBlank()) {
                                Text(
                                    text = if (isTranslationDone && !data.translatedNotes.isNullOrBlank()) {
                                        buildAnnotatedString {
                                            append(stringResource(Res.string.stepByStepAccountOnTodaysTask))
                                            withStyle(
                                                SpanStyle(
                                                    color = AppColors.SkyBlue,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            ) {
                                                append(" (${stringResource(Res.string.aiTranslated)})")
                                            }
                                        }
                                    } else {
                                        buildAnnotatedString { append(stringResource(Res.string.stepByStepAccountOnTodaysTask)) }
                                    },
                                    style = textStyle(12.sp, FontWeight.Normal),
                                    color = AppColors.TextGray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = (if (isTranslationDone && !data.translatedNotes.isNullOrBlank()) data.translatedNotes else data.notes)
                                        ?: "",
                                    style = textStyle(13.sp, FontWeight.Normal),
                                    color = if (isTranslationDone && !data.translatedNotes.isNullOrBlank()) AppColors.SkyBlue else AppColors.Black
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Audio mock
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFFF2EC), RoundedCornerShape(24.dp))
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(32.dp)
                                            .background(AppColors.Primary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("▶", color = Color.White, fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    // Audio visualizer mock
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        repeat(15) { index ->
                                            Box(
                                                modifier = Modifier
                                                    .width(2.dp)
                                                    .height(if (index % 2 == 0) 12.dp else 24.dp)
                                                    .background(
                                                        AppColors.Primary.copy(alpha = 0.6f),
                                                        RoundedCornerShape(1.dp)
                                                    )
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = stringResource(Res.string.txt_000),
                                        style = textStyle(12.sp, FontWeight.Medium),
                                        color = AppColors.TextGray
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    color = Color(0xFFF0F0F0)
                                )
                            }

                            // Attendees
                            if (data.attendees?.isNotEmpty() == true) {
                                val attendeesList = data.attendees ?: emptyList()
                                Text(
                                    text = "ATTENDEES (${attendeesList.size})",
                                    style = textStyle(12.sp, FontWeight.Bold),
                                    color = AppColors.BlackText
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // Table Header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color(0xFFF9F9FB),
                                            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        "Employee Code",
                                        style = textStyle(12.sp, FontWeight.Medium),
                                        color = AppColors.TextGray,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        "Name",
                                        style = textStyle(12.sp, FontWeight.Medium),
                                        color = AppColors.TextGray,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                attendeesList.forEachIndexed { index, attendee ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (index % 2 == 0) Color.White else Color(
                                                    0xFFF9F9FB
                                                )
                                            )
                                            .padding(horizontal = 16.dp, vertical = 12.dp)
                                    ) {
                                        Text(
                                            attendee.employeeCode ?: "-",
                                            style = textStyle(12.sp, FontWeight.Normal),
                                            color = AppColors.Black,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            attendee.employeeName ?: "-",
                                            style = textStyle(12.sp, FontWeight.Normal),
                                            color = AppColors.Black,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    color = Color(0xFFF0F0F0)
                                )
                            }

                            // Evidence
                            if (data.images?.isNotEmpty() == true) {
                                Text(
                                    text = stringResource(Res.string.attendeesEvidence),
                                    style = textStyle(12.sp, FontWeight.Bold),
                                    color = AppColors.BlackText
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                UploadedImagesSection(
                                    images = data.images,
                                    onImageClick = { previewImageUrl = it },
                                    isTranslationDone = isTranslationDone,
                                    showEmptyView = false
                                )
                                Spacer(modifier = Modifier.height(32.dp))
                            }
                        }
                    } // end scrollable Column
                    }
                    ToastHost(
                        visible = infoMessage != null,
                        message = infoMessage.orEmpty(),
                        onDismiss = { infoMessage = null },
                        type = org.example.project.utilites.ToastType.Info,
                        modifier = Modifier.padding(horizontal = 22.dp)
                    )
                }
            }
        }
    }

    if (previewImageUrl != null) {
        org.example.project.ui.components.AppImagePreviewDialog(
            imageUrl = previewImageUrl!!,
            onDismiss = { previewImageUrl = null }
        )
    }
}

@Composable
private fun AnswerBadge(selectedAnswer: Int) {
    // Yes = 1, No = 2, NA = 3
    when (selectedAnswer) {
        1 -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("✓", style = textStyle(14.sp, FontWeight.SemiBold), color = AppColors.Primary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Yes", style = textStyle(12.sp, FontWeight.SemiBold), color = AppColors.Primary)
            }
        }
        2 -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("✕", style = textStyle(14.sp, FontWeight.SemiBold), color = AppColors.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text("No", style = textStyle(12.sp, FontWeight.SemiBold), color = AppColors.Black)
            }
        }
        3 -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⊘", style = textStyle(14.sp, FontWeight.SemiBold), color = AppColors.TextGray)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Not Applicable", style = textStyle(12.sp, FontWeight.Medium), color = AppColors.TextGray)
            }
        }
        else -> {
            Text("-", style = textStyle(12.sp, FontWeight.Medium), color = AppColors.TextGray)
        }
    }
}
