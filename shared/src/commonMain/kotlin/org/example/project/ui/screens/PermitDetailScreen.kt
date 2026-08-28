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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.ic_add_photo
import instaresolv.shared.generated.resources.ic_avatar
import instaresolv.shared.generated.resources.ic_share
import org.example.project.App
import org.example.project.colors.AppColors
import org.example.project.data.model.GroupUser
import org.example.project.data.model.PermitActionDetails
import org.example.project.data.model.PermitDetailData
import org.example.project.data.model.PermitDetailUser
import org.example.project.data.model.PermitStatus
import org.example.project.data.model.PermitFormUserType
import org.example.project.data.model.PermitImage
import org.example.project.data.settings.formatDate
import org.example.project.data.settings.utcToLocal
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppDatePicker
import org.example.project.ui.components.AppImageCreateBox
import org.example.project.ui.components.AppImagePreviewDialog
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.AppMultilineTextField
import org.example.project.ui.components.UploadedImagesSection
import org.example.project.ui.components.AppSignCreateBox
import org.example.project.ui.components.AppStatusDialog
import org.example.project.ui.components.AppTimePicker
import org.example.project.ui.components.AppUserDropdown
import org.example.project.utilites.NavigationBackIcon
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppTextField
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@Composable
fun PermitDetailScreen(
    id: Int,
    onBackClicked: () -> Unit
) {
    val viewModel: PermitDetailViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val userType by viewModel.userType.collectAsState()
    val authorizerName by viewModel.authorizerName.collectAsState()
    val hsePersons by viewModel.hsePersons.collectAsState()
    val selectedHsePerson by viewModel.selectedHsePerson.collectAsState()
    val msraNumber by viewModel.msraNumber.collectAsState()
    val signatureUrl by viewModel.signatureUrl.collectAsState()
    val signatureDate by viewModel.signatureDate.collectAsState()
    val signatureTime by viewModel.signatureTime.collectAsState()
    val additionalPrecautions by viewModel.additionalPrecautions.collectAsState()
    val canCancelOrSuspend by viewModel.canCancelOrSuspend.collectAsState()
    val showActionDialog by viewModel.showActionDialog.collectAsState()
    val actionRemarks by viewModel.actionRemarks.collectAsState()
    val actionImages by viewModel.actionImages.collectAsState()
    val canReactivate by viewModel.canReactivate.collectAsState()
    
    val actionLoading by viewModel.actionLoadingState.collectAsState()
    val isAuthorizing by viewModel.isAuthorizing.collectAsState()
    val successMessage by viewModel.submitSuccessMessage.collectAsState()
    
    val isWorkCompletedVerified by viewModel.isWorkCompletedVerified.collectAsState()
    val closureRemarks by viewModel.closureRemarks.collectAsState()
    val closureImages by viewModel.closureImages.collectAsState()
    val closureSignatureUrl by viewModel.closureSignatureUrl.collectAsState()
    val closureSignatureDate by viewModel.closureSignatureDate.collectAsState()
    val closureSignatureTime by viewModel.closureSignatureTime.collectAsState()
    val isSubmittingClosure by viewModel.isSubmittingClosure.collectAsState()
    
    val certificateClosureSignatureUrl by viewModel.certificateClosureSignatureUrl.collectAsState()
    val certificateClosureDate by viewModel.certificateClosureDate.collectAsState()
    val certificateClosureTime by viewModel.certificateClosureTime.collectAsState()
    val isSubmittingCertificateClosure by viewModel.isSubmittingCertificateClosure.collectAsState()

    var previewImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(id) {
        viewModel.loadPermitDetail(id)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                    text = stringResource(Res.string.permitDetail).uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
            }
        },
        bottomBar = {
            if (uiState is PermitDetailUiState.Success) {
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 22.dp, vertical = 16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            AppBorderButton(
                                title = stringResource(Res.string.generatePdf),
                                onClick = { },
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                modifier = Modifier.weight(1f)
                                    .clickable {  }
                                    .height(48.dp)
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Share", style = textStyle(size = 14.sp, FontWeight.Bold), color = AppColors.Black)
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
        when (val state = uiState) {
            is PermitDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    AppLoader()
                }
            }
            is PermitDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorRetryView(
                        errorMessage = state.message,
                        modifier = Modifier.fillMaxSize(),
                        onRetryClick = { viewModel.loadPermitDetail(id) }
                    )
                }
            }
            is PermitDetailUiState.Success -> {
                val data = state.data
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PermitDetailContent(
                        submitError = state.submitError,
                        data = data,
                        userType = userType,
                        authorizerName = authorizerName,
                        hsePersons = hsePersons,
                        selectedHsePerson = selectedHsePerson,
                        msraNumber = msraNumber,
                        signatureUrl = signatureUrl,
                        signatureDate = signatureDate,
                        signatureTime = signatureTime,
                        additionalPrecautions = additionalPrecautions,
                        viewModel = viewModel,
                        paddingValues = paddingValues,
                        onImageClick = { previewImageUrl = it },
                        canCancelOrSuspend = canCancelOrSuspend,
                        canReactivate = canReactivate,
                        actionLoading = actionLoading,
                        isAuthorizing = isAuthorizing,
                        isWorkCompletedVerified = isWorkCompletedVerified,
                        closureRemarks = closureRemarks,
                        closureImages = closureImages,
                        closureSignatureUrl = closureSignatureUrl,
                        closureSignatureDate = closureSignatureDate,
                        closureSignatureTime = closureSignatureTime,
                        isSubmittingClosure = isSubmittingClosure,
                        certificateClosureSignatureUrl = certificateClosureSignatureUrl,
                        certificateClosureDate = certificateClosureDate,
                        certificateClosureTime = certificateClosureTime,
                        isSubmittingCertificateClosure = isSubmittingCertificateClosure
                    )
                }
            }
        }


        
        if (successMessage != null) {
            AppStatusDialog(
                visible = true,
                title = stringResource(Res.string.success),
                description = successMessage!!,
                buttonText = "OK",
                onDismiss = {
                    viewModel.onDismissSubmitSuccess()
                    onBackClicked()
                }
            )
        }

        if (previewImageUrl != null) {
            AppImagePreviewDialog(
                imageUrl = previewImageUrl!!,
                onDismiss = { previewImageUrl = null }
            )
        }
        
        if (showActionDialog) {
            PermitActionDialog(
                remarks = actionRemarks,
                images = actionImages,
                onRemarksChange = viewModel::onActionRemarksChanged,
                onImageDescriptionChange = viewModel::onImageDescriptionChange,
                onImageSelected = viewModel::onImageSelected,
                onImageRemoved = viewModel::onImageRemoved,
                onAddImageSlot = viewModel::onAddImageSlot,
                onDismiss = viewModel::onActionDialogDismiss,
                onSubmit = { viewModel.submitPermitAction(id) }
            )
        }
    }
}

@Composable
fun PermitDetailContent(
    submitError: String?,
    data: PermitDetailData,
    userType: PermitFormUserType,
    authorizerName: String,
    hsePersons: List<GroupUser>,
    selectedHsePerson: GroupUser?,
    msraNumber: String,
    signatureUrl: String?,
    signatureDate: String,
    signatureTime: String,
    additionalPrecautions: String,
    viewModel: PermitDetailViewModel,
    paddingValues: PaddingValues,
    onImageClick: (String) -> Unit,
    canCancelOrSuspend: Boolean,
    canReactivate: Boolean,
    actionLoading: Int,
    isAuthorizing: Boolean,
    isWorkCompletedVerified: Boolean,
    closureRemarks: String,
    closureImages: List<PermitImage>,
    closureSignatureUrl: String?,
    closureSignatureDate: String,
    closureSignatureTime: String,
    isSubmittingClosure: Boolean,
    certificateClosureSignatureUrl: String?,
    certificateClosureDate: String,
    certificateClosureTime: String,
    isSubmittingCertificateClosure: Boolean
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            PermitHeaderSection(data, userType)
            FacilityProjectSection(data)
            RequestedBySection(data)
            AuthorizedPersonSection(data)
            DateAndTimesSection(data)
            LocationDescriptionSection(data)
            ValiditySectionsSection(data)
            GeneralConditionsSection(data)
            PermitValiditySection(data, onImageClick)
            EvidenceImagesSection(data, onImageClick)

            // Authorization Request Form
            if (userType == PermitFormUserType.AUTHORIZER) {
                AuthorizerFormSection(
                    data = data,
                    authorizerName = authorizerName,
                    hsePersons = hsePersons,
                    selectedHsePerson = selectedHsePerson,
                    msraNumber = msraNumber,
                    signatureUrl = signatureUrl,
                    signatureDate = signatureDate,
                    signatureTime = signatureTime,
                    additionalPrecautions = additionalPrecautions,
                    viewModel = viewModel,
                    isAuthorizing = isAuthorizing
                )
            }

            if (userType == PermitFormUserType.AUTHORIZER_VIEWER) {
                AuthorizerViewerSection(
                    data = data,
                    onImageClick = onImageClick,
                    canCancelOrSuspend = canCancelOrSuspend,
                    canReactivate = canReactivate,
                    actionLoading = actionLoading,
                    viewModel = viewModel
                )
            }

            if (userType == PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE) {
                AuthorizerViewerSection(
                    data = data,
                    onImageClick = onImageClick,
                    canCancelOrSuspend = canCancelOrSuspend,
                    canReactivate = canReactivate,
                    actionLoading = actionLoading,
                    viewModel = viewModel
                )
                RequestForPermitClosureSection(
                    data = data,
                    isWorkCompletedVerified = isWorkCompletedVerified,
                    closureRemarks = closureRemarks,
                    closureImages = closureImages,
                    closureSignatureUrl = closureSignatureUrl,
                    signatureDate = closureSignatureDate,
                    signatureTime = closureSignatureTime,
                    isSubmittingClosure = isSubmittingClosure,
                    viewModel = viewModel
                )
            }

            if (userType == PermitFormUserType.REQUEST_FOR_CERTIFICATE_CLOSURE_VIEWER) {
                AuthorizerViewerSection(
                    data = data,
                    onImageClick = onImageClick,
                    canCancelOrSuspend = canCancelOrSuspend,
                    canReactivate = canReactivate,
                    actionLoading = actionLoading,
                    viewModel = viewModel
                )
                RequestForCertificateClosureViewerSection(data, onImageClick)
                PermitCertificateClosureViewerSection(data, onImageClick)
            }

            if (userType == PermitFormUserType.CERTIFICATE_CLOSURE) {
                AuthorizerViewerSection(
                    data = data,
                    onImageClick = onImageClick,
                    canCancelOrSuspend = canCancelOrSuspend,
                    canReactivate = canReactivate,
                    actionLoading = actionLoading,
                    viewModel = viewModel
                )
                RequestForCertificateClosureViewerSection(data, onImageClick)
                PermitCertificateClosureSection(
                    data = data,
                    userName = authorizerName,
                    certificateClosureSignatureUrl = certificateClosureSignatureUrl,
                    certificateClosureDate = certificateClosureDate,
                    certificateClosureTime = certificateClosureTime,
                    isSubmittingCertificateClosure = isSubmittingCertificateClosure,
                    viewModel = viewModel
                )
            }

            if (userType == PermitFormUserType.NONE) {
                AuthorizerViewerSection(
                    data = data,
                    onImageClick = onImageClick,
                    canCancelOrSuspend = canCancelOrSuspend,
                    canReactivate = canReactivate,
                    actionLoading = actionLoading,
                    viewModel = viewModel
                )
                RequestForCertificateClosureViewerSection(data, onImageClick)
                PermitCertificateClosureViewerSection(data, onImageClick)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        ToastHost(
            visible = submitError != null,
            type = ToastType.Error,
            message = submitError ?: "",
            onDismiss = {
                viewModel.clearSubmitError()
            }
        )
    }
}

@Composable
fun PermitHeaderSection(data: PermitDetailData, userType: PermitFormUserType) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = data.permitType?.permitTypeTitle ?: "N/A",
            style = textStyle(16.sp, FontWeight.Bold),
            color = AppColors.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = data.permitCode ?: "N/A",
            style = textStyle(12.sp, FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        data.permitStatus?.let { statusVal ->
            val status = PermitStatus.fromValue(statusVal)
            if (status != null) {
                Box(
                    modifier = Modifier
                        .background(Color(status.colorHex), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = status.title.uppercase(),
                        style = textStyle(size = 9.sp, weight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
}

@Composable
fun FacilityProjectSection(data: PermitDetailData) {
    Text(
        text = stringResource(Res.string.facilityProject),
        style = textStyle(12.sp, FontWeight.Normal),
        color = AppColors.TextGray
    )
    Spacer(modifier = Modifier.height(8.dp))
    val fac = data.facility ?: data.certificateValidity?.project
    if (fac != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            WebImageView(
                imageUrl = fac.groupImage ?: "",
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = fac.groupName ?: "N/A",
                    style = textStyle(14.sp, FontWeight.SemiBold),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(AppColors.TextGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = fac.groupCode ?: "",
                        style = textStyle(10.sp, FontWeight.Medium),
                        color = Color.White
                    )
                }
            }
        }
    } else {
        Text("N/A", style = textStyle(14.sp, FontWeight.SemiBold), color = AppColors.Black)
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
}

@Composable
fun RequestedBySection(data: PermitDetailData) {
    Text(
        text = stringResource(Res.string.requestedBy),
        style = textStyle(12.sp, FontWeight.Normal),
        color = AppColors.TextGray
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (data.permitRequestedUser?.profileImage != null) {
            WebImageView(
                imageUrl = data.permitRequestedUser.profileImage,
                modifier = Modifier.size(36.dp).clip(CircleShape)
            )
        } else {
            Icon(
                painter = painterResource(Res.drawable.ic_avatar),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = data.permitRequestedUser?.name ?: "N/A",
                style = textStyle(14.sp, FontWeight.SemiBold),
                color = AppColors.Black
            )
            if (data.permitRequestedUser?.email != null) {
                Text(
                    text = data.permitRequestedUser.email,
                    style = textStyle(12.sp, FontWeight.Normal),
                    color = AppColors.TextGray
                )
            }
        }
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
}

@Composable
fun AuthorizedPersonSection(data: PermitDetailData) {
    if (data.certificateValidity?.authorizedPerson != null) {
        Text(
            text = stringResource(Res.string.authorizedPerson),
            style = textStyle(12.sp, FontWeight.Normal),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (data.certificateValidity.authorizedPerson.image != null) {
                WebImageView(
                    imageUrl = data.certificateValidity.authorizedPerson.image,
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.ic_avatar),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = data.certificateValidity.authorizedPerson.name ?: "N/A",
                    style = textStyle(14.sp, FontWeight.SemiBold),
                    color = AppColors.Black
                )
                if (data.certificateValidity.authorizedPerson.email != null) {
                    Text(
                        text = data.certificateValidity.authorizedPerson.email,
                        style = textStyle(12.sp, FontWeight.Normal),
                        color = AppColors.TextGray
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
    }
}

@Composable
fun DateAndTimesSection(data: PermitDetailData) {
    Column() {
        Text(
            text = stringResource(Res.string.requestDate),
            style = textStyle(12.sp, FontWeight.Normal),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = data.certificateValidity?.certificateDate?.let { formatDate(it, "yyyy-MM-dd", "dd MMM yyyy") } ?: "N/A",
            style = textStyle(14.sp, FontWeight.SemiBold),
            color = AppColors.Black
        )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.validFrom),
                style = textStyle(12.sp, FontWeight.Normal),
                color = AppColors.TextGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text =  utcToLocal(
                    data.certificateValidity?.validFrom ?: "N/A",
                    "HH:mm:ss",
                    "hh:mm a"
                ),
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
                text = utcToLocal(
                    data.certificateValidity?.endTime ?: "N/A",
                    "HH:mm:ss",
                    "hh:mm a"
                ),
                style = textStyle(14.sp, FontWeight.SemiBold),
                color = AppColors.Black
            )
        }
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
}

@Composable
fun LocationDescriptionSection(data: PermitDetailData) {
    if (data.certificateValidity?.location != null) {
        Text(
            text = stringResource(Res.string.location),
            style = textStyle(12.sp, FontWeight.Normal),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = data.certificateValidity.location,
            style = textStyle(14.sp, FontWeight.SemiBold),
            color = AppColors.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (data.certificateValidity?.description != null) {
        Text(
            text = stringResource(Res.string.description),
            style = textStyle(12.sp, FontWeight.Normal),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = data.certificateValidity.description,
            style = textStyle(14.sp, FontWeight.SemiBold),
            color = AppColors.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ValiditySectionsSection(data: PermitDetailData) {
    if (!data.certificateValidity?.certificateValiditySections.isNullOrEmpty()) {
        Text(
            text = stringResource(Res.string.validitySections),
            style = textStyle(14.sp, FontWeight.Bold),
            color = AppColors.Primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        data.certificateValidity?.certificateValiditySections?.forEach { section ->
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    text = section.title ?: "",
                    style = textStyle(12.sp, FontWeight.Medium),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = section.answer?.ifBlank { "-" } ?: "-",
                    style = textStyle(12.sp, FontWeight.Bold),
                    color = AppColors.Primary
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
    }
}

@Composable
fun GeneralConditionsSection(data: PermitDetailData) {
    if (!data.certificateValidity?.generalConditions.isNullOrEmpty()) {
        Text(
            text = stringResource(Res.string.generalConditions),
            style = textStyle(14.sp, FontWeight.Bold),
            color = AppColors.Primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        data.certificateValidity?.generalConditions?.forEach { condition ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = condition.title ?: "",
                        style = textStyle(12.sp, FontWeight.Medium),
                        color = AppColors.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when(condition.answer) {
                            1 -> "Yes"
                            2 -> "No"
                            3 -> "N/A"
                            else -> "-"
                        },
                        style = textStyle(12.sp, FontWeight.Bold),
                        color = AppColors.Primary
                    )
                }
                if (!condition.remarks.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Remarks: ${condition.remarks}",
                        style = textStyle(10.sp, FontWeight.Normal),
                        color = AppColors.TextGray
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
    }
}

@Composable
fun PermitValiditySection(data: PermitDetailData, onImageClick: (String) -> Unit) {
    if (data.certificateValidity?.requestContractor != null || data.certificateValidity?.signatureImageUrl != null) {
        Text(
            text = stringResource(Res.string.permitValidity),
            style = textStyle(14.sp, FontWeight.Bold),
            color = AppColors.Primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        if (!data.certificateValidity.requestContractor.isNullOrBlank()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(Res.string.requestorName),
                    style = textStyle(12.sp, FontWeight.Medium),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.certificateValidity.requestContractor,
                    style = textStyle(12.sp, FontWeight.Bold),
                    color = AppColors.Primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        if (!data.certificateValidity.signatureImageUrl.isNullOrBlank()) {
            Text(
                text = stringResource(Res.string.signature),
                style = textStyle(12.sp, FontWeight.Normal),
                color = AppColors.TextGray
            )
            Spacer(modifier = Modifier.height(8.dp))
            WebImageView(
                imageUrl = data.certificateValidity.signatureImageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onImageClick(data.certificateValidity.signatureImageUrl) },
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!data.certificateValidity.requestDate.isNullOrBlank()) {
                Text(
                    text = "Date : ${formatDate(
                        data.certificateValidity.requestDate,
                        "yyyy-MM-dd",
                        "dd MMM yyyy"
                    ) }",
                    style = textStyle(12.sp, FontWeight.Medium),
                    color = AppColors.TextGray
                )
            }
            if (!data.certificateValidity.requestTime.isNullOrBlank()) {
                Text(
                    text = "Time : ${utcToLocal(
                        data.certificateValidity.requestTime,
                        inputFormat = "HH:mm:ss",
                        outputFormat = "hh:mm a"
                    )}",
                    style = textStyle(12.sp, FontWeight.Medium),
                    color = AppColors.TextGray
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
    }
}

@Composable
fun EvidenceImagesSection(data: PermitDetailData, onImageClick: (String) -> Unit) {
    if (!data.certificateValidity?.images.isNullOrEmpty()) {
        UploadedImagesSection(
            images = data.certificateValidity?.images,
            onImageClick = onImageClick,
            title = stringResource(Res.string.images),
            showEmptyView = false
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
    }
}

@Composable
fun AuthorizerFormSection(
    data: PermitDetailData,
    authorizerName: String,
    hsePersons: List<GroupUser>,
    selectedHsePerson: GroupUser?,
    msraNumber: String,
    signatureUrl: String?,
    signatureDate: String,
    signatureTime: String,
    additionalPrecautions: String,
    viewModel: PermitDetailViewModel,
    isAuthorizing: Boolean
) {
    Text(
        text = stringResource(Res.string.authorizationRequest),
        style = textStyle(14.sp, FontWeight.Bold),
        color = AppColors.Primary
    )
    Spacer(modifier = Modifier.height(16.dp))

    AppTextField(
        title = stringResource(Res.string.authorizerName),
        placeholder = stringResource(Res.string.authorizerName),
        value = authorizerName,
        onValueChange = {},
        enabled = false,
        readOnly = true,
        isMandatory = true
    )
    
    Spacer(modifier = Modifier.height(16.dp))

    AppUserDropdown(
        title = stringResource(Res.string.responsibleHsePerson),
        isMandatory = true,
        placeholder = stringResource(Res.string.selectPerson),
        users = hsePersons,
        selectedUser = selectedHsePerson,
        onUserSelected = viewModel::onHsePersonSelected
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    AppTextField(
        title = stringResource(Res.string.msraNumber),
        placeholder = stringResource(Res.string.enterMsraNumber),
        value = msraNumber,
        onValueChange = viewModel::onMsraNumberChanged,
        isMandatory = true
    )
    
    Spacer(modifier = Modifier.height(16.dp))

    AppSignCreateBox(
        signatureUrl = signatureUrl,
        onSignatureUploaded = viewModel::onSignatureUploaded,
        onRemoveSignatureClick = viewModel::onRemoveSignatureClick
    )

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    append("Time")
                },
                style = textStyle(12.sp, FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            AppTimePicker(
                text = stringResource(Res.string.txt_0000),
                selectedTime = signatureTime,
                onTimeSelected = {  },
                enabled = false
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    append("Date")
                },
                style = textStyle(12.sp, FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            AppDatePicker(
                text = signatureDate.ifBlank { "YYYY-MM-DD" },
                onDateSelected = { },
                selectedDateMillis = null,
                enabled = false
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))

    AppMultilineTextField(
        title = stringResource(Res.string.additionalPrecautions),
        placeholder = stringResource(Res.string.enterAdditionalPrecautions),
        value = additionalPrecautions,
        onValueChange = viewModel::onAdditionalPrecautionsChanged
    )
    Spacer(modifier = Modifier.height(24.dp))
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        AppPrimaryButton(
            title = stringResource(Res.string.submit),
            isLoading = isAuthorizing,
            onClick = {
                data.permitId?.let { viewModel.authorizePermit(it) }
            }
        )
    }
}

@Composable
fun AuthorizerViewerSection(
    data: PermitDetailData,
    onImageClick: (String) -> Unit,
    canCancelOrSuspend: Boolean,
    canReactivate: Boolean,
    actionLoading: Int,
    viewModel: PermitDetailViewModel
) {
    Text(
        text = stringResource(Res.string.authorizationRequest),
        style = textStyle(14.sp, FontWeight.Bold),
        color = AppColors.Primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text(
            text = stringResource(Res.string.requestorName),
            style = textStyle(12.sp, FontWeight.Medium),
            color = AppColors.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = data.authorizationRequest?.authorizerName ?: "-",
            style = textStyle(12.sp, FontWeight.Bold),
            color = AppColors.Primary
        )
    }
    
    if (!data.authorizationRequest?.msraNumber.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Text(
                text = stringResource(Res.string.msraNumber),
                style = textStyle(12.sp, FontWeight.Medium),
                color = AppColors.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = data.authorizationRequest?.msraNumber ?: "-",
                style = textStyle(12.sp, FontWeight.Bold),
                color = AppColors.Primary
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))

    // Responsible HSE Person
    if (data.authorizationRequest?.responsibleHSEPerson != null) {
        Text(
            text = stringResource(Res.string.responsibleHsePerson),
            style = textStyle(12.sp, FontWeight.Normal),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (data.authorizationRequest.responsibleHSEPerson.image != null) {
                WebImageView(
                    imageUrl = data.authorizationRequest.responsibleHSEPerson.image,
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.ic_avatar),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = data.authorizationRequest.responsibleHSEPerson.name ?: "N/A",
                    style = textStyle(14.sp, FontWeight.SemiBold),
                    color = AppColors.Black
                )
                if (data.authorizationRequest.responsibleHSEPerson.email != null) {
                    Text(
                        text = data.authorizationRequest.responsibleHSEPerson.email,
                        style = textStyle(12.sp, FontWeight.Normal),
                        color = AppColors.TextGray
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
    }

    if (!data.authorizationRequest?.signatureImageUrl.isNullOrBlank()) {
        Text(
            text = stringResource(Res.string.signature),
            style = textStyle(12.sp, FontWeight.Normal),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        WebImageView(
            imageUrl = data.authorizationRequest?.signatureImageUrl ?: "",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { data.authorizationRequest?.signatureImageUrl?.let { onImageClick(it) } },
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (!data.authorizationRequest?.authorizedDate.isNullOrBlank()) {
            Text(
                text = "Date : ${formatDate(
                    data.authorizationRequest.authorizedDate,
                    "yyyy-MM-dd",
                    "dd MMM yyyy"
                )}",
                style = textStyle(12.sp, FontWeight.Medium),
                color = AppColors.TextGray
            )
        }
        if (!data.authorizationRequest?.authorizedTime.isNullOrBlank()) {
            Text(
                text = "Time : ${utcToLocal(
                    data.authorizationRequest.authorizedTime,
                    inputFormat = "HH:mm:ss",
                    outputFormat = "hh:mm a"
                ) }",
                style = textStyle(12.sp, FontWeight.Medium),
                color = AppColors.TextGray
            )
        }
    }
    
    if (!data.authorizationRequest?.notes.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
        Text(
            text = stringResource(Res.string.additionalPrecautions),
            style = textStyle(12.sp, FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = data.authorizationRequest.notes ?: "-",
            style = textStyle(14.sp, FontWeight.SemiBold),
            color = AppColors.Black
        )
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

    // Action Details (Cancellations, Suspensions, Reactivations)
    if (data.permitCancellationDetails != null) {
        ActionDetailSection("Cancellation Details", listOf(data.permitCancellationDetails), onImageClick)
    }

    if (!data.permitCancelledUsers.isNullOrEmpty()) {
        val user = data.permitCancelledUsers.lastOrNull()
        if (user != null) {
            PermitActionUserCard(user, "Cancelled User")
        }
    }

    if (!data.permitSuspensionDetails.isNullOrEmpty()) {
        ActionDetailSection("Suspension Details", data.permitSuspensionDetails, onImageClick)
    }

    if (data.permitStatus == 4 && !data.permitSuspendedUsers.isNullOrEmpty()) {
        val user = data.permitSuspendedUsers.lastOrNull()
        if (user != null) {
            PermitActionUserCard(user, "Suspended User")
        }
    }

    if (data.permitStatus != 4 && data.permitStatus != 3) {
        if (!data.permitReactivationDetails.isNullOrEmpty()) {
            val lastDetail = data.permitReactivationDetails.last()
            ActionDetailSection("Reactivation Details", listOf(lastDetail), onImageClick)
        }
    }

    if (canCancelOrSuspend) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppPrimaryButton(
                title = stringResource(Res.string.cancel),
                onClick = { viewModel.onActionClick(1) },
                isLoading = actionLoading == 1,
                modifier = Modifier.weight(1f).height(48.dp),
                color = Color(0xFFD32F2F)
            )

            AppPrimaryButton(
                title = stringResource(Res.string.suspend),
                onClick = { viewModel.onActionClick(2) },
                isLoading = actionLoading == 2,
                modifier = Modifier.weight(1f).height(48.dp),
                color = Color(0xFFE5A93C)
            )
        }
    }

    if (canReactivate) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            AppPrimaryButton(
                title = stringResource(Res.string.reactivate),
                onClick = { viewModel.onActionClick(3) },
                isLoading = actionLoading == 3,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                color = Color(0xFF2E6AC6)
            )
        }
    }
}

@Composable
fun RequestForCertificateClosureViewerSection(data: PermitDetailData, onImageClick: (String) -> Unit) {
    val closureDetails = data.requestForCertificateClosure ?: return
    
    Text(
        text = stringResource(Res.string.requestForCertificateClosure),
        style = textStyle(14.sp, FontWeight.Bold),
        color = AppColors.Primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    
    if (closureDetails.images?.isNotEmpty() == true) {
        UploadedImagesSection(
            images = closureDetails.images,
            onImageClick = onImageClick,
            title = stringResource(Res.string.images),
            showEmptyView = false
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (!closureDetails.remarks.isNullOrBlank()) {
        Text(
            text = stringResource(Res.string.remarks),
            style = textStyle(12.sp, FontWeight.Medium),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = closureDetails.remarks,
            style = textStyle(14.sp, FontWeight.SemiBold),
            color = AppColors.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (!closureDetails.signatureImageUrl.isNullOrBlank()) {
        Text(
            text = stringResource(Res.string.signature),
            style = textStyle(12.sp, FontWeight.Normal),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        WebImageView(
            imageUrl = closureDetails.signatureImageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onImageClick(closureDetails.signatureImageUrl) },
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (!closureDetails.requestDate.isNullOrBlank()) {
            Text(
                text = "Date : ${formatDate(
                    closureDetails.requestDate,
                    "yyyy-MM-dd",
                    "dd MMM yyyy"
                )}",
                style = textStyle(12.sp, FontWeight.Medium),
                color = AppColors.TextGray
            )
        }
        if (!closureDetails.requestTime.isNullOrBlank()) {
            Text(
                text = "Time : ${utcToLocal(
                    closureDetails.requestTime,
                    inputFormat = "HH:mm:ss",
                    outputFormat = "hh:mm a"
                )}",
                style = textStyle(12.sp, FontWeight.Medium),
                color = AppColors.TextGray
            )
        }
    }
    
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
}

@Composable
fun RequestForPermitClosureSection(
    data: PermitDetailData,
    isWorkCompletedVerified: Boolean,
    closureRemarks: String,
    closureImages: List<PermitImage>,
    closureSignatureUrl: String?,
    signatureDate: String,
    signatureTime: String,
    isSubmittingClosure: Boolean,
    viewModel: PermitDetailViewModel
) {
    Text(
        text = stringResource(Res.string.requestForCertificateClosure),
        style = textStyle(14.sp, FontWeight.Bold),
        color = AppColors.Primary
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Checkbox
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isWorkCompletedVerified,
            onCheckedChange = viewModel::onWorkCompletedVerifiedChanged,
            colors = CheckboxDefaults.colors(
                AppColors.Primary
            )
        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = buildAnnotatedString {
                append("I verify the work has been completed and all required controls were implemented")
                withStyle(
                    style = SpanStyle(color = Color.Red)
                ) {
                    append(" *")
                }
            },
            style = textStyle(
                10.sp,
                FontWeight.Medium,
                fontStyle = FontStyle.Italic
            ),
            color = AppColors.Black
        )
    }
    
    Spacer(modifier = Modifier.height(16.dp))

    // Images
    closureImages.forEachIndexed { index, image ->
        AppImageCreateBox(
            imageUrl = image.image,
            description = image.description ?: "",
            onDescriptionChange = { viewModel.onClosureImageDescriptionChange(index, it) },
            onImageUploaded = { viewModel.onClosureImageSelected(index, it) },
            onRemoveImageClick = { viewModel.onClosureImageRemoved(index) }
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
    if (closureImages.size < 6) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { viewModel.onAddClosureImageSlot() }.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(painter = painterResource(Res.drawable.ic_add), contentDescription = null, tint = AppColors.Primary)
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
    
    Spacer(modifier = Modifier.height(16.dp))
    
    AppMultilineTextField(
        title = stringResource(Res.string.remarks),
        placeholder = stringResource(Res.string.enterRemarks),
        value = closureRemarks,
        onValueChange = viewModel::onClosureRemarksChanged
    )
    
    Spacer(modifier = Modifier.height(16.dp))

    AppSignCreateBox(
        signatureUrl = closureSignatureUrl,
        onSignatureUploaded = viewModel::onClosureSignatureUploaded,
        onRemoveSignatureClick = viewModel::onRemoveClosureSignatureClick
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(Res.string.time), style = textStyle(12.sp, FontWeight.SemiBold))
            Spacer(modifier = Modifier.height(8.dp))
            AppTimePicker(text = stringResource(Res.string.txt_0000), selectedTime = signatureTime, onTimeSelected = { }, enabled = false)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(Res.string.date), style = textStyle(12.sp, FontWeight.SemiBold))
            Spacer(modifier = Modifier.height(8.dp))
            AppDatePicker(text = signatureDate.ifBlank { "YYYY-MM-DD" }, onDateSelected = { }, selectedDateMillis = null, enabled = false)
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        AppPrimaryButton(
            title = stringResource(Res.string.submit),
            isLoading = isSubmittingClosure,
            onClick = {
                data.permitId?.let {
                    viewModel.submitPermitClosureRequest(
                        it,
                        isWorkCompletedVerified
                    )
                }
            }
        )
    }
}


@Composable
fun ActionDetailSection(
    title: String,
    detailsList: List<PermitActionDetails>,
    onImageClick: (String) -> Unit
) {
    Text(
        text = title,
        style = textStyle(14.sp, FontWeight.Bold),
        color = AppColors.Primary
    )
    Spacer(modifier = Modifier.height(12.dp))
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 250.dp)
            .verticalScroll(rememberScrollState())
    ) {
        detailsList.forEachIndexed { index, details ->
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            if (details.actionedBy != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (details.actionedBy.profileImage != null) {
                        WebImageView(
                            imageUrl = details.actionedBy.profileImage,
                            modifier = Modifier.size(24.dp).clip(CircleShape)
                        )
                    } else {
                        Icon(
                            painter = painterResource(Res.drawable.ic_avatar),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.Unspecified
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Actioned by: ${details.actionedBy.name}",
                        style = textStyle(12.sp, FontWeight.Medium),
                        color = AppColors.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (!details.remarks.isNullOrEmpty()) {
                Text(
                    text = "Remarks: ${details.remarks}",
                    style = textStyle(12.sp, FontWeight.Normal),
                    color = AppColors.TextGray
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (!details.images.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    details.images.forEach { img ->
                        if (img.image != null) {
                            WebImageView(
                                imageUrl = img.image,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onImageClick(img.image) },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
        if (index < detailsList.size - 1) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF0F0F0))
        }
    }
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
}

@Composable
fun PermitActionUserCard(
    user: PermitDetailUser,
    title: String
) {
    Text(
        text = title,
        style = textStyle(14.sp, FontWeight.Bold),
        color = AppColors.Primary
    )
    Spacer(modifier = Modifier.height(12.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (user.profileImage != null) {
                WebImageView(
                    imageUrl = user.profileImage,
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.ic_avatar),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Unspecified
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = user.name ?: "N/A",
                    style = textStyle(14.sp, FontWeight.SemiBold),
                    color = AppColors.Black
                )
                if (user.email != null) {
                    Text(
                        text = user.email,
                        style = textStyle(12.sp, FontWeight.Normal),
                        color = AppColors.TextGray
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun PermitActionDialog(
    remarks: String,
    images: List<PermitImage>,
    onRemarksChange: (String) -> Unit,
    onImageDescriptionChange: (Int, String) -> Unit,
    onImageSelected: (Int, String) -> Unit,
    onImageRemoved: (Int) -> Unit,
    onAddImageSlot: () -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = stringResource(Res.string.permitAction),
                        style = textStyle(16.sp, FontWeight.Bold),
                        color = AppColors.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AppMultilineTextField(
                        title = stringResource(Res.string.remarks),
                        placeholder = stringResource(Res.string.enterRemarks),
                        value = remarks,
                        onValueChange = onRemarksChange
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(Res.string.images),
                        style = textStyle(12.sp, FontWeight.Medium),
                        color = AppColors.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    images.forEachIndexed { index, image ->
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
                            AppImageCreateBox(
                                imageUrl = image.image,
                                description = image.description ?: "",
                                onDescriptionChange = { onImageDescriptionChange(index, it) },
                                onImageUploaded = {
                                    onImageSelected(index, it)
                                },
                                onRemoveImageClick = {
                                    onImageRemoved(index)
                                }
                            )
                        }
                    }
                    if (images.size < 6) {
                        TextButton(
                            onClick = onAddImageSlot,
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

                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppBorderButton(
                        title = stringResource(Res.string.cancel),
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )
                    AppPrimaryButton(
                        title = stringResource(Res.string.submit),
                        onClick = onSubmit,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PermitCertificateClosureSection(
    data: PermitDetailData,
    userName: String,
    certificateClosureSignatureUrl: String?,
    certificateClosureDate: String,
    certificateClosureTime: String,
    isSubmittingCertificateClosure: Boolean,
    viewModel: PermitDetailViewModel
) {
    Text(
        text = stringResource(Res.string.permitClosure),
        style = textStyle(14.sp, FontWeight.Bold),
        color = AppColors.Primary
    )
    Spacer(modifier = Modifier.height(16.dp))
    AppTextField(
        value = userName,
        onValueChange = {},
        title = stringResource(Res.string.authorizerName),
        placeholder = stringResource(Res.string.authorizerName),
        modifier = Modifier.fillMaxWidth(),
        enabled = false
    )
    
    Spacer(modifier = Modifier.height(16.dp))

    AppSignCreateBox(
        onSignatureUploaded = viewModel::onCertificateClosureSignatureUploaded,
        onRemoveSignatureClick = viewModel::onRemoveCertificateClosureSignatureClick,
        signatureUrl = certificateClosureSignatureUrl
    )

    Spacer(modifier = Modifier.height(12.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(Res.string.time), style = textStyle(12.sp, FontWeight.SemiBold))
            Spacer(modifier = Modifier.height(8.dp))
            AppTimePicker(
                text = stringResource(Res.string.txt_0000),
                selectedTime = certificateClosureTime,
                onTimeSelected = { },
                enabled = false
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(Res.string.date), style = textStyle(12.sp, FontWeight.SemiBold))
            Spacer(modifier = Modifier.height(8.dp))
            AppDatePicker(
                text = certificateClosureDate.ifBlank { "YYYY-MM-DD" },
                onDateSelected = { },
                selectedDateMillis = null,
                enabled = false
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        AppPrimaryButton(
            title = stringResource(Res.string.submit),
            isLoading = isSubmittingCertificateClosure,
            onClick = { data.permitId?.let { viewModel.submitPermitCertificateClosure(it) } },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        )
    }
}

@Composable
fun PermitCertificateClosureViewerSection(data: PermitDetailData, onImageClick: (String) -> Unit) {
    val closureDetails = data.certificateClosure ?: return

    Text(
        text = stringResource(Res.string.permitClosure),
        style = textStyle(14.sp, FontWeight.Bold),
        color = AppColors.Primary
    )
    Spacer(modifier = Modifier.height(16.dp))

    if (!closureDetails.contractorName.isNullOrBlank()) {
        Text(
            text = stringResource(Res.string.authorizerName),
            style = textStyle(12.sp, FontWeight.Medium),
            color = AppColors.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = closureDetails.contractorName,
            style = textStyle(12.sp, FontWeight.Bold),
            color = AppColors.Primary
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (!closureDetails.signatureImageUrl.isNullOrBlank()) {
        Text(
            text = stringResource(Res.string.signature),
            style = textStyle(12.sp, FontWeight.Normal),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(8.dp))
        WebImageView(
            imageUrl = closureDetails.signatureImageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onImageClick(closureDetails.signatureImageUrl) },
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (!closureDetails.closureDate.isNullOrBlank()) {
            Text(
                text = "Date : ${formatDate(
                    closureDetails.closureDate,
                    "yyyy-MM-dd",
                    "dd MMM yyyy"
                )}",
                style = textStyle(12.sp, FontWeight.Medium),
                color = AppColors.TextGray
            )
        }
        if (!closureDetails.closureTime.isNullOrBlank()) {
            Text(
                text = "Time : ${utcToLocal(
                    closureDetails.closureTime,
                    inputFormat = "HH:mm:ss",
                    outputFormat = "hh:mm a"
                )}",
                style = textStyle(12.sp, FontWeight.Medium),
                color = AppColors.TextGray
            )
        }
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
}
