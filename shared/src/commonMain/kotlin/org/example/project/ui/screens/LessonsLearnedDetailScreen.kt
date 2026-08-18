package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_share
import instaresolv.shared.generated.resources.ic_translate
import instaresolv.shared.generated.resources.ic_translate_done
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import org.example.project.colors.AppColors
import org.example.project.data.model.LessonLearnedDetailResponseData
import org.example.project.data.settings.formatDate
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.UploadedImagesSection
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@Composable
fun LessonsLearnedDetailScreen(
    id: Int,
    onClose: () -> Unit
) {
    val viewModel: LessonsLearnedDetailViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    var previewImageUrl by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }
    val noTranslationText = stringResource(Res.string.noTranslationInfoAvailable)

    val isGeneratingPdf by viewModel.isGeneratingPdf.collectAsState()
    val pdfUrl by viewModel.pdfUrl.collectAsState()
    val pdfToastMessage by viewModel.pdfToastMessage.collectAsState()
    val fileDownloader = org.example.project.utilites.rememberFileDownloader()

    LaunchedEffect(pdfUrl) {
        pdfUrl?.let { url ->
            try {
                val fileName = "Lesson_Learned_${Clock.System.now().toEpochMilliseconds()}.pdf"
                fileDownloader.downloadFile(url, fileName)
                viewModel.setPdfToastMessage("Downloading Lesson Learned Report")
            } catch (e: Exception) {
                // Handle error
            }
            viewModel.clearPdfUrl()
        }
    }

    LaunchedEffect(id) {
        viewModel.loadLessonLearnedDetail(id)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (uiState is LessonsLearnedDetailUiState.Success) {
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
                            onClick = { viewModel.generatePdf(id) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 40.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
        ) {
            when (val state = uiState) {
                is LessonsLearnedDetailUiState.Loading -> {
                    AppLoader()
                }
                is LessonsLearnedDetailUiState.Error -> {
                    ErrorRetryView(
                        errorMessage = state.message,
                        onRetryClick = { viewModel.loadLessonLearnedDetail(id) }
                    )
                }
                is LessonsLearnedDetailUiState.Success -> {
                    LessonsLearnedDetailContent(
                        data = state.data,
                        onImageClick = { previewImageUrl = it },
                        onNoTranslation = { infoMessage = noTranslationText }
                    )
                }
            }

            if (isGeneratingPdf) {
                org.example.project.ui.components.PdfGenerationLoader()
            }

            previewImageUrl?.let { url ->
                org.example.project.ui.components.AppImagePreviewDialog(
                    imageUrl = url,
                    onDismiss = { previewImageUrl = null }
                )
            }

            ToastHost(
                visible = infoMessage != null,
                message = infoMessage.orEmpty(),
                onDismiss = { infoMessage = null },
                type = ToastType.Info,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .padding(horizontal = 22.dp)
            )

            ToastHost(
                visible = pdfToastMessage != null,
                message = pdfToastMessage.orEmpty(),
                onDismiss = { viewModel.clearPdfToastMessage() },
                type = ToastType.Success,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .padding(horizontal = 22.dp)
            )
        }
    }
}

@Composable
fun LessonsLearnedDetailContent(
    data: LessonLearnedDetailResponseData,
    onImageClick: (String) -> Unit,
    onNoTranslation: () -> Unit
) {
    var isTranslationDone by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp, vertical = 16.dp),
    ) {
        // Drag handle
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(CircleShape)
                .background(Color(0xFFE5E5EA))
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(12.dp))

        // Title row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
                Text(
                    text = stringResource(Res.string.title),
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = data.title ?: "-",
                    style = textStyle(size = 16.sp, weight = FontWeight.Bold),
                    color = AppColors.Black
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isTranslationDone) AppColors.Primary else Color(0xFF8F9098))
                    .clickable {
                        val hasTranslation = !data.translatedDescription.isNullOrBlank() ||
                            data.images?.any { !it.translatedImageDescription.isNullOrBlank() } == true
                        if (hasTranslation) {
                            isTranslationDone = !isTranslationDone
                        } else {
                            onNoTranslation()
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
        Spacer(Modifier.height(24.dp))
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {

        // Project/Facility section
        Text(
            text = stringResource(Res.string.project),
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(11.dp))
        Row {
            WebImageView(
                imageUrl = data.facilities?.groupImage,
                modifier = Modifier.size(42.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = data.facilities?.groupName ?: "-",
                    style = textStyle(size = 13.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Black
                )
                if (!data.facilities?.groupCode.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Gray)
                    ) {
                        Text(
                            text = data.facilities?.groupCode ?: "",
                            modifier = Modifier.padding(vertical = 2.dp, horizontal = 5.dp),
                            style = textStyle(
                                size = 10.sp,
                                weight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))

        // Reported By
        Text(
            text = stringResource(Res.string.reportedBy),
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            WebImageView(
                imageUrl = "",
                modifier = Modifier.size(25.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = data.reportedBy ?: "-",
                style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                color = AppColors.Black
            )
        }

        Spacer(Modifier.height(24.dp))

        // Date
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.date),
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (data.createdAt.isNullOrEmpty()) "-" else formatDate(
                        data.createdAt,
                        inputPattern = "yyyy-MM-dd HH:mm:ss",
                        outputPattern = "dd MMM yyyy"
                    ),
                    style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Black
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Description
        Text(
            text = if (isTranslationDone && !data.translatedDescription.isNullOrBlank()) {
                buildAnnotatedString {
                    append(stringResource(Res.string.description))
                    withStyle(SpanStyle(color = AppColors.SkyBlue, fontWeight = FontWeight.Medium)) {
                        append(" (${stringResource(Res.string.aiTranslated)})")
                    }
                }
            } else {
                buildAnnotatedString { append(stringResource(Res.string.description)) }
            },
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = (if (isTranslationDone && !data.translatedDescription.isNullOrBlank()) data.translatedDescription else data.description)
                ?.takeIf { it.isNotBlank() } ?: "-",
            style = textStyle(size = 14.sp, weight = FontWeight.Medium),
            color = if (isTranslationDone && !data.translatedDescription.isNullOrBlank()) AppColors.SkyBlue else AppColors.Black
        )

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))

        // Uploaded Images
        UploadedImagesSection(
            images = data.images,
            onImageClick = onImageClick,
            isTranslationDone = isTranslationDone
        )
        } // end scrollable Column
    }
}
