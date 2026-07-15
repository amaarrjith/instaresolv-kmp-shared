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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_avatar
import instaresolv.shared.generated.resources.ic_share
import org.example.project.App
import org.example.project.colors.AppColors
import org.example.project.data.model.GroupUser
import org.example.project.data.model.PermitActionDetails
import org.example.project.data.model.PermitDetailData
import org.example.project.data.model.PermitStatus
import org.example.project.data.model.PermitFormUserType
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppDatePicker
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.AppMultilineTextField
import org.example.project.ui.components.AppSignCreateBox
import org.example.project.ui.components.AppTimePicker
import org.example.project.ui.components.AppUserDropdown
import org.example.project.utilites.NavigationBackIcon
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppTextField
import org.example.project.utilites.ErrorRetryView
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

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
                    text = "Permit Detail".uppercase(),
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 22.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppBorderButton(
                            title = "Generate PDF",
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
                        onRetryClick = { viewModel.loadPermitDetail(id) }
                    )
                }
            }
            is PermitDetailUiState.Success -> {
                val data = state.data
                PermitDetailContent(
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
                    onImageClick = { previewImageUrl = it }
                )
            }
            is PermitDetailUiState.SubmitSuccess -> {
                org.example.project.ui.components.AppStatusDialog(
                    visible = true,
                    title = "Success",
                    description = state.message,
                    buttonText = "OK",
                    onDismiss = {
                        onBackClicked()
                    }
                )
            }
        }

        if (previewImageUrl != null) {
            org.example.project.ui.components.AppImagePreviewDialog(
                imageUrl = previewImageUrl!!,
                onDismiss = { previewImageUrl = null }
            )
        }
    }
}

@Composable
fun PermitDetailContent(
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
    onImageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues)
            .padding(horizontal = 22.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Header Info
        Column(modifier = Modifier.fillMaxWidth()) {
            if (userType != PermitFormUserType.NONE) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Role: ${userType.name.replace("_", " ")}",
                        style = textStyle(size = 10.sp, weight = FontWeight.SemiBold),
                        color = Color(0xFF1976D2)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
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

        // Facility / Project
        Text(
            text = "Facility / Project",
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

        // Requested By
        Text(
            text = "Requested By",
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
                if (data.permitRequestedUser?.designation != null) {
                    Text(
                        text = data.permitRequestedUser.designation,
                        style = textStyle(12.sp, FontWeight.Normal),
                        color = AppColors.TextGray
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))

        // Authorized Person
        if (data.certificateValidity?.authorizedPerson != null) {
            Text(
                text = "Authorized Person",
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

        // Date & Times

            Column() {
                Text(
                    text = "Request Date",
                    style = textStyle(12.sp, FontWeight.Normal),
                    color = AppColors.TextGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.createdAt?.let { org.example.project.data.settings.formatDate(it, "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy") } ?: "N/A",
                    style = textStyle(14.sp, FontWeight.SemiBold),
                    color = AppColors.Black
                )
            }
        Spacer(modifier = Modifier.height(16.dp))
//        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Valid From",
                    style = textStyle(12.sp, FontWeight.Normal),
                    color = AppColors.TextGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.certificateValidity?.validFrom ?: "N/A",
                    style = textStyle(14.sp, FontWeight.SemiBold),
                    color = AppColors.Black
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "End Time",
                    style = textStyle(12.sp, FontWeight.Normal),
                    color = AppColors.TextGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.certificateValidity?.endTime ?: "N/A",
                    style = textStyle(14.sp, FontWeight.SemiBold),
                    color = AppColors.Black
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
        // Location & Description
        if (data.certificateValidity?.location != null) {
            Text(
                text = "Location",
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
                text = "Description",
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

        // Validity Sections
        if (!data.certificateValidity?.certificateValiditySections.isNullOrEmpty()) {
            Text(
                text = "Validity Sections",
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
                        text = section.answer ?: "-",
                        style = textStyle(12.sp, FontWeight.Bold),
                        color = AppColors.Primary
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
        }

        // General Conditions
        if (!data.certificateValidity?.generalConditions.isNullOrEmpty()) {
            Text(
                text = "General Conditions",
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

        // Permit Validity
        if (data.certificateValidity?.requestContractor != null || data.certificateValidity?.signatureImageUrl != null) {
            Text(
                text = "Permit Validity",
                style = textStyle(14.sp, FontWeight.Bold),
                color = AppColors.Primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            if (!data.certificateValidity.requestContractor.isNullOrBlank()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Requestor Name",
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
                WebImageView(
                    imageUrl = data.certificateValidity.signatureImageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(data.certificateValidity.signatureImageUrl) },
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!data.certificateValidity.requestDate.isNullOrBlank()) {
                    Text(
                        text = "Date: ${data.certificateValidity.requestDate}",
                        style = textStyle(12.sp, FontWeight.Medium),
                        color = AppColors.TextGray
                    )
                }
                if (!data.certificateValidity.requestTime.isNullOrBlank()) {
                    Text(
                        text = "Time: ${data.certificateValidity.requestTime}",
                        style = textStyle(12.sp, FontWeight.Medium),
                        color = AppColors.TextGray
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
        }

        // Evidence Images
        if (!data.certificateValidity?.images.isNullOrEmpty()) {
            Text(
                text = "Images",
                style = textStyle(14.sp, FontWeight.Bold),
                color = AppColors.Primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                data.certificateValidity?.images?.forEach { imageDetail ->
                    if (imageDetail.image?.isNotBlank() == true) {
                        Column {
                            WebImageView(
                                imageUrl = imageDetail.image,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onImageClick(imageDetail.image) },
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                            if (!imageDetail.description.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = imageDetail.description,
                                    style = textStyle(14.sp, FontWeight.Normal),
                                    color = AppColors.Black
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
        }

        // Action Details (Cancellations, Suspensions, Reactivations)
        if (data.permitCancellationDetails != null) {
            ActionDetailSection("Cancellation Details", listOf(data.permitCancellationDetails), onImageClick)
        }
        
        if (!data.permitSuspensionDetails.isNullOrEmpty()) {
            ActionDetailSection("Suspension Details", data.permitSuspensionDetails, onImageClick)
        }
        
        if (!data.permitReactivationDetails.isNullOrEmpty()) {
            ActionDetailSection("Reactivation Details", data.permitReactivationDetails, onImageClick)
        }
        
        // Authorization Request Form
        if (userType == PermitFormUserType.AUTHORIZER) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Authorization Request",
                style = textStyle(14.sp, FontWeight.Bold),
                color = AppColors.Primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                title = "Authorizer Name",
                placeholder = "Authorizer Name",
                value = authorizerName,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                isMandatory = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            AppUserDropdown(
                title = "Responsible HSE Person",
                placeholder = "Select Person",
                users = hsePersons,
                selectedUser = selectedHsePerson,
                onUserSelected = viewModel::onHsePersonSelected
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AppTextField(
                title = "MSRA Number",
                placeholder = "Enter MSRA Number",
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
                        text = "00 : 00",
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
                title = "Additional Precautions",
                placeholder = "Enter Additional Precautions",
                value = additionalPrecautions,
                onValueChange = viewModel::onAdditionalPrecautionsChanged
            )
            Spacer(modifier = Modifier.height(24.dp))

            AppPrimaryButton(
                title = "Submit",
                onClick = {
                    data.permitId?.let { viewModel.authorizePermit(it) }
                }
            )
        }

        if (userType == PermitFormUserType.AUTHORIZER_VIEWER) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Authorization Request",
                style = textStyle(14.sp, FontWeight.Bold),
                color = AppColors.Primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Requestor Name",
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
            Spacer(modifier = Modifier.height(12.dp))

        }
        
        Spacer(modifier = Modifier.height(32.dp))
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
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
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
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F0F0))
}
