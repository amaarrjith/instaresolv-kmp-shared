package org.example.project.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.ic_camera
import instaresolv.shared.generated.resources.ic_right_icon
import org.example.project.utilites.rtlScale
import instaresolv.shared.generated.resources.ic_share
import instaresolv.shared.generated.resources.ic_translate
import instaresolv.shared.generated.resources.ic_translate_done

import org.example.project.colors.AppColors
import org.example.project.data.model.ObservationDetailResponse
import org.example.project.data.model.Project
import org.example.project.data.settings.formatDate
import org.example.project.typography.textStyle
import org.example.project.ui.ObservationStatus
import org.example.project.ui.ProjectListCard
import org.example.project.ui.components.AppAudioPlayer
import org.example.project.ui.components.AppImageCreateBox
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.AppMultilineTextField
import org.example.project.ui.components.UploadedImagesSection
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.example.project.ui.components.AppStatusDialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import kotlin.time.Clock
import instaresolv.shared.generated.resources.*

@Composable
fun ObservationDetailScreen(
    observationId: Int,
    onRefreshList: () -> Unit,
    onBackClicked: () -> Unit,
    startWithCloseForm: Boolean = false
) {
    val viewModel: ObservationDetailViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val closeUiState by viewModel.closeUiState.collectAsState()
    
    var isClosingObservation by remember { mutableStateOf(startWithCloseForm) }
    var closeDescription by remember { mutableStateOf("") }
    val closeImages = remember { androidx.compose.runtime.mutableStateListOf(ObservationImage()) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }
    var previewImageUrl by remember { mutableStateOf<String?>(null) }
    var isTranslationDone by remember { mutableStateOf(false) }
    val noTranslationText = stringResource(Res.string.noTranslationInfoAvailable)
    val isGeneratingPdf by viewModel.isGeneratingPdf.collectAsState()
    val pdfUrl by viewModel.pdfUrl.collectAsState()
    val pdfToastMessage by viewModel.pdfToastMessage.collectAsState()
    val fileDownloader = org.example.project.utilites.rememberFileDownloader()

    LaunchedEffect(pdfUrl) {
        pdfUrl?.let { url ->
            try {
                val fileName = "Observation_PDF_${Clock.System.now().toEpochMilliseconds()}.pdf"
                fileDownloader.downloadFile(url, fileName)
                viewModel.setPdfToastMessage("Downloading Observation Report")
            } catch (e: Exception) {
                // Handle error
            }
            viewModel.clearPdfUrl()
        }
    }

    LaunchedEffect(observationId) {
        viewModel.loadObservationDetail(observationId)
    }

    LaunchedEffect(uiState) {
        if (uiState is ObservationDetailUiState.Success) {
            val successState = uiState as ObservationDetailUiState.Success
            if (!successState.detail.translatedDescription.isNullOrEmpty()) {
                isTranslationDone = true
            }
        }
    }

    LaunchedEffect(closeUiState) {
        when (val state = closeUiState) {
            is CloseObservationState.Success -> {
                showSuccessDialog = true
                viewModel.resetCloseState()
            }
            is CloseObservationState.Error -> {
                errorMessage = state.message
                viewModel.resetCloseState()
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.Transparent, // To match BottomSheet dimming behind it if any
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (uiState is ObservationDetailUiState.Success) {
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 22.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isClosingObservation) {
                            AppBorderButton(
                                title = stringResource(Res.string.back),
                                onClick = { isClosingObservation = false },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(16.dp))
                            AppBorderButton(
                                title = stringResource(Res.string.close),
                                onClick = {
                                    if (closeDescription.isBlank()) {
                                        errorMessage = "Description is mandatory"
                                    } else {
                                        viewModel.closeObservation(observationId, closeDescription, closeImages)
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            AppBorderButton(
                                title = stringResource(Res.string.generatePdf),
                                onClick = {
                                    viewModel.generatePdf(observationId)
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
                                Text(stringResource(Res.string.share), style = textStyle(size = 14.sp, weight = FontWeight.Bold), color = AppColors.Black)
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Bottom bar padding
                .padding(top = 40.dp) // Offset from top to look like a sheet
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White)
        ) {
            when (val state = uiState) {
                is ObservationDetailUiState.Idle, is ObservationDetailUiState.Loading -> {
                    AppLoader()
                }
                is ObservationDetailUiState.Error -> {
                    ErrorRetryView(
                        errorMessage = state.message,
                        modifier = Modifier.fillMaxSize(),
                        onRetryClick = {
                            viewModel.loadObservationDetail(observationId)
                        }
                    )
                }
                is ObservationDetailUiState.Success -> {
                    ObservationDetailContent(
                        detail = state.detail,
                        translatedAudioUrl = state.translatedAudioUrl,
                        isClosingObservation = isClosingObservation,
                        onCloseObservationClick = { isClosingObservation = true },
                        closeDescription = closeDescription,
                        onDescriptionChange = { closeDescription = it },
                        closeImages = closeImages,
                        onImageClick = { previewImageUrl = it },
                        isTranslationDone = isTranslationDone,
                        onTranslate = {
                            val hasTranslation = !state.detail.translatedDescription.isNullOrBlank() ||
                                state.detail.imageDescription?.any { !it.translatedImageDescription.isNullOrBlank() } == true
                            if (hasTranslation) {
                                isTranslationDone = !isTranslationDone
                            } else {
                                infoMessage = noTranslationText
                            }
                        },
                        onTranslateAudio = {
                            viewModel.translateAudio(
                                state.detail.audioUrl
                                ) { infoMessage = it }
                        },
                        isTranslatingAudio = state.isTranslatingAudio
                    )
                }
            }

            ToastHost(
                visible = errorMessage != null,
                message = errorMessage ?: "",
                onDismiss = { errorMessage = null },
                type = ToastType.Error,
                modifier = Modifier.padding(horizontal = 22.dp).align(Alignment.BottomCenter)
            )

            ToastHost(
                visible = infoMessage != null,
                message = infoMessage ?: "",
                onDismiss = { infoMessage = null },
                type = ToastType.Info,
                modifier = Modifier.padding(horizontal = 22.dp).align(Alignment.BottomCenter)
            )
            
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
                visible = pdfToastMessage != null,
                message = pdfToastMessage.orEmpty(),
                onDismiss = { viewModel.clearPdfToastMessage() },
                type = ToastType.Success,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
            )
        }
        
        if (showSuccessDialog) {
            AppStatusDialog(
                visible = true,
                title = stringResource(Res.string.success),
                description = "Observation closed successfully.",
                buttonText = "OK",
                onDismiss = {
                    onRefreshList()
                    showSuccessDialog = false
                    onBackClicked()
                }
            )
        }
    }
}

@Composable
fun ObservationDetailContent(
    detail: ObservationDetailResponse,
    translatedAudioUrl: String?,
    isTranslationDone: Boolean,
    onTranslate: () -> Unit,
    isClosingObservation: Boolean,
    onCloseObservationClick: () -> Unit,
    closeDescription: String,
    onDescriptionChange: (String) -> Unit,
    closeImages: androidx.compose.runtime.snapshots.SnapshotStateList<ObservationImage>,
    onImageClick: (String) -> Unit,
    onTranslateAudio: () -> Unit,
    isTranslatingAudio: Boolean
) {
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
                    text = detail.observationTitle ?: "Untitled",
                    style = textStyle(size = 16.sp, weight = FontWeight.Bold),
                    color = AppColors.Black
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isTranslationDone) AppColors.Primary else Color(0xFF8F9098))
                    .clickable { onTranslate() },
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
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(Res.string.project),
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(11.dp))
            if (detail.group != null) {
                Row {
                    WebImageView(
                        imageUrl = detail.group?.groupImage,
                        modifier = Modifier.size(42.dp)
                            .clip(RoundedCornerShape(15))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = detail.group?.groupName ?: "",
                            style = textStyle(size = 13.sp, weight = FontWeight.SemiBold),
                            color = AppColors.Black
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Gray)
                        ) {
                            Text(
                                text = detail.group?.groupCode.toString(),
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
            } else {
                Text(
                    text = "-",
                    style = textStyle(size = 13.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Black
                )
            }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))

        AnimatedContent(
            targetState = isClosingObservation,
            transitionSpec = {
                if (targetState) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                }
            }
        ) { isClosing ->
            if (isClosing) {
                CloseObservationForm(
                    description = closeDescription,
                    onDescriptionChange = onDescriptionChange,
                    observationImages = closeImages
                )
            } else {
                ObservationDetailInfo(detail, onCloseObservationClick, onImageClick, isTranslationDone, translatedAudioUrl, onTranslateAudio, isTranslatingAudio)
            }
        }
        }
    }
}

@Composable
fun ObservationDetailInfo(
    detail: ObservationDetailResponse,
    onCloseObservationClick: () -> Unit,
    onImageClick: (String) -> Unit,
    isTranslationDone: Boolean,
    translatedAudioUrl: String?,
    onTranslateAudio: () -> Unit,
    isTranslatingAudio: Boolean

) {
    Column {
        Text(
            text = stringResource(Res.string.reportedBy),
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            WebImageView(
                imageUrl = "", // Using responsiblePerson image as fallback for mock UI
                modifier = Modifier.size(25.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = detail.reportedBy ?: "-",
                style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                color = AppColors.Black
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.responsiblePerson),
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WebImageView(
                imageUrl = detail.responsiblePerson?.image,
                modifier = Modifier.size(25.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = detail.responsiblePerson?.name.takeUnless { it.isNullOrEmpty() } ?: detail.responsiblePersonName.takeUnless { it.isNullOrEmpty() } ?: "-",
                style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                color = AppColors.Black
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.responsiblePersonEmail),
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = detail.responsiblePerson?.email ?: "-",
            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.location),
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = detail.location.takeUnless { it.isNullOrEmpty() } ?: "-",
            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
            color = AppColors.Black
        )

        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.date),
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatDate(detail.date ?: "-", inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", outputPattern = "dd MMM yyyy"),
                    style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Black
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(Res.string.status),
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(4.dp))
                val status = ObservationStatus.fromId(detail.status ?: -1)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background((status.backgroundColor))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(status.title).uppercase(),
                        style = textStyle(size = 10.sp, weight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.description),
            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = detail.description.takeUnless { it.isNullOrEmpty() } ?: "-",
            style = textStyle(size = 14.sp, weight = FontWeight.Medium),
            color = AppColors.Black
        )

        if (isTranslationDone) {
            Spacer(Modifier.height(24.dp))

            Text(
                text = buildAnnotatedString {
                    append(stringResource(Res.string.description))
                    withStyle(
                        SpanStyle(
                            color = AppColors.SkyBlue,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(" (${(stringResource(Res.string.aiTranslated))})")
                    }
                },
                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                color = AppColors.TextGray
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = detail.translatedDescription ?: "",
                style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                color = AppColors.SkyBlue
            )
        }

        if (!detail.audioUrl.isNullOrEmpty()) {
            Spacer(Modifier.height(24.dp))
            if (!translatedAudioUrl.isNullOrEmpty()) {
                Text(
                    text = buildAnnotatedString {
                        append("${stringResource(Res.string.audio)} ")
                        withStyle(
                            SpanStyle(
                                color = AppColors.TextGray,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append("(${stringResource(Res.string.original)})")
                        }
                    },
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(8.dp))
            }
            AppAudioPlayer(
                filePath = detail.audioUrl, 
                isTranslationRequired = translatedAudioUrl.isNullOrEmpty(), 
                onTranslateButtonClick = {
                    onTranslateAudio()
                }
            )
        }

        if (isTranslatingAudio) {
            Spacer(Modifier.height(24.dp))
            AppLoader()
        }

        if (!translatedAudioUrl.isNullOrEmpty()) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = buildAnnotatedString {
                    append("${stringResource(Res.string.audio)} ")
                    withStyle(
                        SpanStyle(
                            color = AppColors.SkyBlue,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(" (${(stringResource(Res.string.aiTranslated))})")
                    }
                },
                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                color = AppColors.TextGray
            )
            Spacer(Modifier.height(8.dp))
            AppAudioPlayer(filePath = translatedAudioUrl)
        }
        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))

        UploadedImagesSection(
            images = detail.imageDescription,
            onImageClick = onImageClick,
            isTranslationDone = isTranslationDone
        )

        Spacer(Modifier.height(24.dp))
        HorizontalDivider(color = Color(0xFFF0F0F5))
        Spacer(Modifier.height(24.dp))
        if (ObservationStatus.fromId(detail.status ?: -1) == ObservationStatus.OPEN) {
            Text(
                text = stringResource(Res.string.actions),
                style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                color = AppColors.Black
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().clickable { onCloseObservationClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.closeObservation),
                        style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                        color = AppColors.Primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(Res.string.closeThisObservationWithDetailed),
                        style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                        color = AppColors.TextGray
                    )
                }
                Image(
                    painter = painterResource(Res.drawable.ic_right_icon),
                    contentDescription = null,
                    modifier = Modifier.rtlScale()
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        if (detail.closeDetails != null) {
            val closeDetails = detail.closeDetails
            // Title row: "Observation Closeout" on left, time ago on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.observationCloseout),
                    style = textStyle(size = 16.sp, weight = FontWeight.Bold),
                    color = AppColors.Primary
                )
                if (!closeDetails.date.isNullOrEmpty()) {
                    Text(
                        text = org.example.project.data.settings.timeAgo(closeDetails.date, isUtc = true),
                        style = textStyle(size = 11.sp, weight = FontWeight.Medium),
                        color = AppColors.TextGray
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Column() {
                Text(
                    text = stringResource(Res.string.date),
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatDate(detail.closeDetails.date ?: "-", inputPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", outputPattern = "dd MMM yyyy"),
                    style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                    color = AppColors.Black
                )
            }

            Spacer(Modifier.height(16.dp))

            // Description
            Text(
                text = stringResource(Res.string.description),
                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                color = AppColors.TextGray
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = closeDetails.closeDescription.takeUnless { it.isNullOrEmpty() } ?: "-",
                style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                color = AppColors.Black
            )

            Spacer(Modifier.height(16.dp))
            UploadedImagesSection(
                images = closeDetails.imageDescription,
                onImageClick = onImageClick,
                isTranslationDone = isTranslationDone
            )

            // Closed By
            if (closeDetails.closedBy != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(Res.string.closedBy),
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WebImageView(
                        imageUrl = closeDetails.closedBy.image,
                        modifier = Modifier.size(25.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = closeDetails.closedBy.name.takeUnless { it.isNullOrEmpty() } ?: "-",
                        style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                        color = AppColors.Black
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun CloseObservationForm(
    description: String,
    onDescriptionChange: (String) -> Unit,
    observationImages: androidx.compose.runtime.snapshots.SnapshotStateList<ObservationImage>
) {
    Column {
        Text(
            text = stringResource(Res.string.closeObservation),
            style = textStyle(size = 16.sp, weight = FontWeight.Bold),
            color = AppColors.Black
        )
        Spacer(Modifier.height(16.dp))
        
        AppMultilineTextField(
            value = description,
            onValueChange = onDescriptionChange,
            title = stringResource(Res.string.description),
            placeholder = stringResource(Res.string.enterDescription1)
        )
        
        Spacer(Modifier.height(24.dp))
        
        observationImages.forEachIndexed { index, observationImage ->
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Upload Image ${index + 1}",
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.SemiBold
                    ),
                    color = AppColors.Black
                )
                org.example.project.ui.components.AppImageCreateBox(
                    imageUrl = observationImage.imageUrl,
                    description = observationImage.description,
                    onDescriptionChange = { newDesc ->
                        observationImages[index] = observationImage.copy(description = newDesc)
                    },
                    onImageUploaded = { newUrl ->
                        observationImages[index] = observationImage.copy(imageUrl = newUrl)
                    },
                    onRemoveImageClick = {
                        observationImages.removeAt(index)
                    }
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        
        if (observationImages.size < 6) {
            androidx.compose.material3.TextButton(
                onClick = { observationImages.add(ObservationImage()) },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_add),
                    contentDescription = stringResource(Res.string.addImage),
                    modifier = Modifier.size(15.dp),
                    tint = AppColors.Primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.addImage),
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.SemiBold
                    ),
                    color = AppColors.Primary
                )
            }
        }
    }
}
