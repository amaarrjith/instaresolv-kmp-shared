package org.example.project.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.rotate
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.ui.text.style.TextAlign
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_calendar
import instaresolv.shared.generated.resources.ic_filter
import instaresolv.shared.generated.resources.ic_arrow_left
import org.example.project.utilites.rtlScale
import instaresolv.shared.generated.resources.ic_permit_work
import org.example.project.colors.AppColors
import org.example.project.data.model.PendingActionItem
import org.example.project.data.model.PendingActionStatusType
import org.example.project.data.model.PermitPendingActionItem
import org.example.project.data.model.PermitStatus
import org.example.project.data.settings.formatDate
import org.example.project.data.settings.timeAgo
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.PermitActionBottomSheet
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.NavigationBackIcon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import kotlin.time.Clock
import org.example.project.utilites.ToastHost
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*
import org.example.project.ui.components.ObservationActionBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingActionListScreen(
    onBackClicked: () -> Unit,
    onPermitClick: (Int) -> Unit = {}
) {
    val viewModel: PendingActionListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedFilters by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var selectedPermitFilters by remember { mutableStateOf<Set<Int>>(emptySet()) }
    
    val sheetState = rememberModalBottomSheetState()
    var selectedActionForSheet by remember { mutableStateOf<PendingActionItem?>(null) }
    var selectedPermitForSheet by remember { mutableStateOf<PermitPendingActionItem?>(null) }

    // Observation action modals
    var viewReportObservationId by remember { mutableStateOf<Int?>(null) }
    var closeObservationId by remember { mutableStateOf<Int?>(null) }
    var showRequestDeleteSheet by remember { mutableStateOf(false) }
    var showRequestResponsiblePersonSheet by remember { mutableStateOf(false) }
    var pendingActionForModal by remember { mutableStateOf<PendingActionItem?>(null) }
    val groupUsers by viewModel.groupUsers.collectAsState()
    var actionSuccessMessage by remember { mutableStateOf<String?>(null) }
    var actionErrorMessage by remember { mutableStateOf<String?>(null) }
    
    val isGeneratingPdf by viewModel.isGeneratingPdf.collectAsState()
    val pdfUrl by viewModel.pdfUrl.collectAsState()
    val pdfToastMessage by viewModel.pdfToastMessage.collectAsState()
    val pdfErrorToastMessage by viewModel.pdfErrorToastMessage.collectAsState()
    val fileDownloader = org.example.project.utilites.rememberFileDownloader()

    androidx.compose.runtime.LaunchedEffect(pdfUrl) {
        pdfUrl?.let { url ->
            try {
                val fileName = "Permit_PDF_${Clock.System.now().toEpochMilliseconds()}.pdf"
                fileDownloader.downloadFile(url, fileName)
                viewModel.setPdfToastMessage("Downloading Permit Report")
            } catch (e: Exception) {
                viewModel.setPdfErrorToastMessage("Failed to download Permit Report")
            }
            viewModel.clearPdfUrl()
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                Row (
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NavigationBackIcon(
                        onBackClicked
                    )
                    Text(
                        text = stringResource(Res.string.pendingActions).uppercase(),
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Bold
                        ),
                        color = AppColors.Black
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(Res.drawable.ic_filter),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 20.dp).clickable {
                            showFilterSheet = true
                        }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    TabItem(
                        title = stringResource(Res.string.generalActions),
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    TabItem(
                        title = stringResource(Res.string.permitActions),
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                }
                
//                HorizontalDivider(thickness = 1.dp, color = AppColors.TextGray.copy(alpha = 0.3f))
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .padding(top = 20.dp)
                ) {
                    if (selectedTab == 0) {
                        if (uiState.isLoading) {
                            AppLoader()
                        } else if (uiState.error != null) {
                            ErrorRetryView(
                                errorMessage = uiState.error ?: "",
                                onRetryClick = { viewModel.fetchPendingActions() }
                            )
                        } else if (uiState.pendingActions.isEmpty()) {
                            EmptyScreenView("No pending actions found.")
                        } else {
                            val filteredActions = if (selectedFilters.isEmpty()) {
                                uiState.pendingActions
                            } else {
                                uiState.pendingActions.filter { selectedFilters.contains(it.type) }
                            }
                            
                            if (filteredActions.isEmpty()) {
                                EmptyScreenView("No pending actions found.")
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(15.dp)
                                ) {
                                    items(filteredActions) { action ->
                                        PendingActionListItem(
                                            action = action,
                                            onClick = { selectedActionForSheet = action }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Permit Actions
                        if (uiState.isPermitLoading) {
                            AppLoader()
                        } else if (uiState.permitError != null) {
                            ErrorRetryView(
                                errorMessage = uiState.permitError ?: "",
                                onRetryClick = { viewModel.fetchPermitPendingActions() }
                            )
                        } else if (uiState.permitPendingActions.isEmpty()) {
                            EmptyScreenView("No permit actions found.")
                        } else {
                            val filteredPermitActions = if (selectedPermitFilters.isEmpty()) {
                                uiState.permitPendingActions
                            } else {
                                uiState.permitPendingActions.filter { selectedPermitFilters.contains(it.status) }
                            }
                            
                            if (filteredPermitActions.isEmpty()) {
                                EmptyScreenView("No permit actions found.")
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(15.dp)
                                ) {
                                    items(filteredPermitActions) { item ->
                                        PermitPendingActionItem(
                                            item = item,
                                            onClick = { selectedPermitForSheet = item }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (showFilterSheet) {
            var tempFilters by remember { mutableStateOf(if (selectedTab == 0) selectedFilters else selectedPermitFilters) }
            
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.filterBy),
                        style = textStyle(size = 18.sp, weight = FontWeight.Bold),
                        color = AppColors.Black
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    val filters = if (selectedTab == 0) {
                        listOf(
                            PendingActionStatusType.OPEN_OBSERVATION to "Open Observation",
                            PendingActionStatusType.REQUEST_TO_JOIN_GROUP to "Request to Join Group",
                            PendingActionStatusType.OBSERVATION_RESPONSIBILITY_CHANGE to "Observation Responsibility Change",
                            PendingActionStatusType.REQUEST_TO_DELETE_OBSERVATION to "Request to Delete Observation",
                            PendingActionStatusType.REVIEW_OBSERVATION_CLOSEOUT to "Review Observation Closeout"
                        )
                    } else {
                        org.example.project.data.model.PermitStatus.entries.map {
                            it.value to it.title
                        }
                    }
                    
                    Column(
                        modifier = Modifier.weight(1f, fill = false).verticalScroll(androidx.compose.foundation.rememberScrollState())
                    ) {
                        filters.forEach { (type, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        tempFilters = if (tempFilters.contains(type)) tempFilters - type else tempFilters + type
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = tempFilters.contains(type),
                                    onClick = {
                                        tempFilters = if (tempFilters.contains(type)) tempFilters - type else tempFilters + type
                                    },
                                    modifier = Modifier.size(20.dp),
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = AppColors.Primary,
                                        unselectedColor = AppColors.TextGray
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = label,
                                    style = textStyle(
                                        size = 14.sp,
                                        weight = if (tempFilters.contains(type)) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    color = if (tempFilters.contains(type)) AppColors.Primary else AppColors.Black
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).clickable { showFilterSheet = false }.padding(15.dp), contentAlignment = Alignment.Center) {
                            Text(text = stringResource(Res.string.cancel), style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                        }
                        Box(modifier = Modifier.width(1.dp).height(20.dp).background(AppColors.TextGray.copy(alpha = 0.3f)))
                        Box(modifier = Modifier.weight(1f).clickable { 
                            if (selectedTab == 0) {
                                selectedFilters = tempFilters
                            } else {
                                selectedPermitFilters = tempFilters
                            }
                            showFilterSheet = false 
                        }.padding(15.dp), contentAlignment = Alignment.Center) {
                            Text(text = stringResource(Res.string.apply), style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.Primary)
                        }
                    }
                }
            }
        }
    
        if (selectedActionForSheet != null) {
            val action = selectedActionForSheet!!
            ObservationActionBottomSheet(
                showSheet = true,
                actionType = action.type,
                onDismiss = { selectedActionForSheet = null },
                onActionClick = { actionName ->
                    when(actionName) {
                        "View Report" -> {
                            viewReportObservationId = action.contentId
                        }
                        "Generate PDF" -> {
                            viewModel.generateObservationPdf(action.contentId)
                        }
                        "Close Observation" -> {
                            closeObservationId = action.contentId
                        }
                        "Request Observation Responsible Person Change" -> {
                            pendingActionForModal = action
                            if (action.groupId != null && action.groupCode != null) {
                                viewModel.fetchGroupUsers(action.groupId, action.groupCode)
                            }
                            showRequestResponsiblePersonSheet = true
                        }
                        "Request to Delete Observation" -> {
                            pendingActionForModal = action
                            showRequestDeleteSheet = true
                        }
                        // Add handlers for Approve/Reject here if they actually do something, right now original code just closed the modal.
                    }
                }
            )
        }
    
        if (selectedPermitForSheet != null) {
            val permitId = selectedPermitForSheet?.permitId
            PermitActionBottomSheet(
                showSheet = true,
                onDismiss = { selectedPermitForSheet = null },
                onActionClick = { action ->
                    when (action) {
                        "View Permit" -> {
                            if (permitId != null) {
                                onPermitClick(permitId)
                            }
                            selectedPermitForSheet = null
                        }
                        "Generate PDF" -> {
                            if (permitId != null) {
                                viewModel.generatePermitPDF(permitId)
                            }
                            selectedPermitForSheet = null
                        }
                    }
                }
            )
        }
    
        if (isGeneratingPdf) {
            org.example.project.ui.components.PdfGenerationLoader()
        }

        // View Report modal
        if (viewReportObservationId != null) {
            val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewReportObservationId = null },
                sheetState = detailSheetState,
                containerColor = Color.White,
                dragHandle = null
            ) {
                Box(modifier = Modifier.fillMaxHeight(0.9f)) {
                    ObservationDetailScreen(
                        observationId = viewReportObservationId!!,
                        onBackClicked = { viewReportObservationId = null },
                        onRefreshList = {
                            viewReportObservationId = null
                            viewModel.fetchPendingActions()
                        }
                    )
                }
            }
        }

        // Close Observation modal
        if (closeObservationId != null) {
            val closeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { closeObservationId = null },
                sheetState = closeSheetState,
                containerColor = Color.White,
                dragHandle = null
            ) {
                Box(modifier = Modifier.fillMaxHeight(0.9f)) {
                    ObservationDetailScreen(
                        observationId = closeObservationId!!,
                        onBackClicked = { closeObservationId = null },
                        onRefreshList = {
                            closeObservationId = null
                            viewModel.fetchPendingActions()
                        },
                        startWithCloseForm = true
                    )
                }
            }
        }

        // Request to Delete Observation modal
        if (showRequestDeleteSheet && pendingActionForModal != null) {
            val deleteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showRequestDeleteSheet = false },
                sheetState = deleteSheetState,
                containerColor = Color.White
            ) {
                org.example.project.ui.components.RequestDeleteObservationView(
                    onBackClicked = { showRequestDeleteSheet = false },
                    onContinueClicked = { justification ->
                        val observationId = pendingActionForModal!!.contentId
                        viewModel.requestToDeleteObservation(
                            observationId = observationId,
                            justification = justification,
                            onSuccess = {
                                showRequestDeleteSheet = false
                                actionSuccessMessage = "Request to delete observation submitted successfully."
                                viewModel.fetchPendingActions()
                            },
                            onError = { err -> actionErrorMessage = err }
                        )
                    }
                )
            }
        }

        // Request Responsible Person Change modal
        if (showRequestResponsiblePersonSheet && pendingActionForModal != null) {
            val responsibleSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showRequestResponsiblePersonSheet = false },
                sheetState = responsibleSheetState,
                containerColor = Color.White
            ) {
                org.example.project.ui.components.RequestResponsiblePersonChangeView(
                    users = groupUsers,
                    onBackClicked = { showRequestResponsiblePersonSheet = false },
                    onContinueClicked = { justification, responsiblePersonId ->
                        val observationId = pendingActionForModal!!.contentId
                        viewModel.requestResponsiblePersonChange(
                            observationId = observationId,
                            justification = justification,
                            responsiblePerson = responsiblePersonId,
                            onSuccess = {
                                showRequestResponsiblePersonSheet = false
                                actionSuccessMessage = "Request to change responsible person submitted successfully."
                                viewModel.fetchPendingActions()
                            },
                            onError = { err -> actionErrorMessage = err }
                        )
                    }
                )
            }
        }

        actionSuccessMessage?.let { msg ->
            org.example.project.ui.components.AppStatusDialog(
                visible = true,
                title = "Success",
                description = msg,
                buttonText = "OK",
                onDismiss = { actionSuccessMessage = null }
            )
        }

        actionErrorMessage?.let { msg ->
            Box(modifier = Modifier.fillMaxWidth()) {
                ToastHost(
                    visible = true,
                    message = msg,
                    onDismiss = { actionErrorMessage = null },
                    type = org.example.project.utilites.ToastType.Error
                )
            }
        }
    
        ToastHost(
            modifier = Modifier.padding(horizontal = 22.dp),
            visible = pdfToastMessage != null || pdfErrorToastMessage != null,
            type = if (pdfErrorToastMessage != null) org.example.project.utilites.ToastType.Error else org.example.project.utilites.ToastType.Success,
            message = pdfErrorToastMessage ?: pdfToastMessage ?: "",
            onDismiss = { viewModel.clearToasts() }
        )
    }
}

@Composable
fun TabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() }.padding(bottom = 4.dp).width(IntrinsicSize.Max)
    ) {
        Text(
            text = title,
            style = textStyle(
                size = 16.sp,
                weight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (isSelected) AppColors.Primary else AppColors.TextGray
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(AppColors.Primary)
            )
        }
    }
}

@Composable
fun PendingActionListItem(
    action: PendingActionItem,
    isEditable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, AppColors.TextGray),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp)
        ) {

            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                StatusBadge(
                    type = action.type,
                    isEditable = isEditable
                )

                Spacer(modifier = Modifier.weight(1f))

                if (!action.date.isNullOrEmpty()) {
                    Text(
                        text = timeAgo(action.date),
                        style = textStyle(
                            size = 12.sp,
                            weight = FontWeight.Normal
                        ),
                        color = AppColors.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(13.dp))

            // Date + Group Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(Res.drawable.ic_calendar),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Red)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (!action.date.isNullOrEmpty())
                        org.example.project.data.settings.formatDate(
                            action.date,
                            "",
                            "dd MMM yyyy"
                        ) else "",
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.Normal
                    ),
                    color = AppColors.Black
                )

                if (!action.groupCode.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.width(18.dp))

                    Text(
                        text = action.groupCode,
                        style = textStyle(
                            size = 12.sp,
                            weight = FontWeight.Normal
                        ),
                        color = AppColors.Black
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(13.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = AppColors.TextGray
            )

            Spacer(modifier = Modifier.height(13.dp))

            // Description
            if (!action.description.isNullOrEmpty()) {
                Text(
                    text = action.description,
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.Normal
                    ),
                    color = AppColors.Black
                )
            }
        }
    }
}

@Composable
fun StatusBadge(type: Int, isEditable: Boolean) {
    val openObs = stringResource(Res.string.openObservation)
    val joinProject = stringResource(Res.string.requestToJoinProject)
    val responsibilityChange = stringResource(Res.string.observationResponsibilityChange)
    val deleteObs = stringResource(Res.string.requestToDeleteObservation)
    val reviewCloseout = stringResource(Res.string.reviewObservationCloseout)

    val (text, bgColor, textColor) = when (type) {
        PendingActionStatusType.OPEN_OBSERVATION ->
            Triple(openObs, Color(0xFFE6FAF8), Color(0xFF28D29F))
        PendingActionStatusType.REQUEST_TO_JOIN_GROUP ->
            Triple(joinProject, Color(0xFFE4F7FF), Color(0xFF00B7FF))
        PendingActionStatusType.OBSERVATION_RESPONSIBILITY_CHANGE ->
            Triple(responsibilityChange, Color(0xFFFFEBE1), Color(0xFFFF846B))
        PendingActionStatusType.REQUEST_TO_DELETE_OBSERVATION ->
            Triple(deleteObs, Color(0xFFF5F6FF), Color(0xFF536DFF))
        PendingActionStatusType.REVIEW_OBSERVATION_CLOSEOUT ->
            Triple(reviewCloseout, Color(0xFFFDF0D8), Color(0xFFF7B231))
        else -> Triple("", Color.Transparent, Color.Transparent)
    }
    if (text.isEmpty()) return

    Box(
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(3.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = textStyle(
                weight = FontWeight.Normal,
                size = 12.sp,
            ),
            color = textColor
        )
    }
}

@Composable
fun ActionRow(title: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = textStyle(size = 14.sp, weight = FontWeight.Normal),
            color = AppColors.Black,
            textAlign = TextAlign.Start
        )
        Image(
            painter = painterResource(Res.drawable.ic_arrow_left),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AppColors.Black),
            modifier = Modifier.rotate(180f).size(20.dp).rtlScale()
        )
    }
}

@Composable
fun PermitPendingActionItem(item: PermitPendingActionItem, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, AppColors.TextGray.copy(alpha = 0.4f)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            // Top Row: Status badge + time ago
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.status?.let { statusId ->
                    PermitStatusBadge(statusId)
                }
                Spacer(modifier = Modifier.weight(1f))
                if (!item.createdAt.isNullOrEmpty()) {
                    Text(
                        text = timeAgo(item.createdAt, isUtc = true),
                        style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                        color = AppColors.TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Permit Code
            if (!item.permitCode.isNullOrEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(Res.drawable.ic_permit_work),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(AppColors.Primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.permitCode,
                        style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                        color = AppColors.BlackText
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Permit Type
            item.permitType?.let { type ->
                Text(
                    text = type.title,
                    style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                    color = AppColors.TextGray
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            HorizontalDivider(thickness = 1.dp, color = AppColors.TextGray.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(10.dp))

            // Date + Group Code row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_calendar),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Red)
                )
                Spacer(modifier = Modifier.width(6.dp))
                if (!item.createdAt.isNullOrEmpty()) {
                    Text(
                        text = formatDate(item.createdAt, "yyyy-MM-dd HH:mm:ss", "dd MMM yyyy"),
                        style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                        color = AppColors.Black
                    )
                }
                if (!item.groupCode.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = item.groupCode,
                        style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                        color = AppColors.Black
                    )
                }
            }
        }
    }
}

@Composable
fun PermitStatusBadge(statusId: Int) {
    val permitStatus = PermitStatus.fromValue(statusId)
    val (bgColor, label) = if (permitStatus != null) {
        Color(permitStatus.colorHex).copy(alpha = 0.15f) to permitStatus.title
    } else {
        Color(0xFFE5E5E5) to "Unknown"
    }
    val textColor = if (permitStatus != null) Color(permitStatus.colorHex) else AppColors.TextGray

    Box(
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(3.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = textStyle(weight = FontWeight.Normal, size = 12.sp),
            color = textColor
        )
    }
}