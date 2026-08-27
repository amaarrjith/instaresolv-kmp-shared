package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.settings.formatDate
import org.jetbrains.compose.resources.DrawableResource
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_bell
import instaresolv.shared.generated.resources.ic_user
import instaresolv.shared.generated.resources.ic_clock
import instaresolv.shared.generated.resources.ic_pipe
import instaresolv.shared.generated.resources.ic_calendar
import instaresolv.shared.generated.resources.assigned_to_me
import instaresolv.shared.generated.resources.view_all
import instaresolv.shared.generated.resources.action_overview
import instaresolv.shared.generated.resources.ic_audit_inspection
import instaresolv.shared.generated.resources.ic_permit_to_work
import instaresolv.shared.generated.resources.ic_observations
import instaresolv.shared.generated.resources.ic_incidents
import instaresolv.shared.generated.resources.ic_location
import instaresolv.shared.generated.resources.ic_right_icon
import org.example.project.utilites.rtlScale
import instaresolv.shared.generated.resources.ic_violations
import instaresolv.shared.generated.resources.ic_training
import org.example.project.colors.AppColors
import org.example.project.data.model.ActionsOverview
import org.example.project.data.model.AssignedToMe
import org.example.project.data.model.PermitStatus
import org.example.project.data.settings.timeAgo
import org.example.project.homescreen.HomeScreenViewModel
import org.example.project.profile.ProfileViewModel
import org.example.project.typography.textStyle
import org.example.project.ui.components.PdfGenerationLoader
import org.example.project.ui.components.WebImageView
import org.example.project.ui.screens.PermitStatusBadge
import org.example.project.utilites.ToastHost
import org.example.project.utilites.rememberFileDownloader
import org.koin.compose.koinInject
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*
import org.example.project.data.model.PendingActionStatusType
import org.example.project.ui.components.AppErrorDialog
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.AppStatusDialog
import org.jetbrains.compose.resources.StringResource
import org.example.project.ui.components.ObservationActionBottomSheet
import org.example.project.ui.components.RequestDeleteObservationView
import org.example.project.ui.components.RequestResponsiblePersonChangeView
import org.example.project.ui.screens.ObservationDetailScreen
import org.example.project.utilites.ToastType

@Composable
fun HomeScreenContentView(
    assignedToMe: AssignedToMe? = null,
    actionOverview: ActionsOverview? = null,
    pullDownRefresh: () -> Unit,
    silentRefresh: () -> Unit = {},
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    isRefreshing: Boolean,
    onClickModule: (ActionOverview) -> Unit,
    onPendingActionViewAllClick: () -> Unit = {},
    onPermitClick: (Int) -> Unit = {},
    onNavigateToProject: (Int, String) -> Unit = { _, _ -> },
    onLogout: () -> Unit = {}
) {
    val viewModel: ProfileViewModel = koinInject()
    val vm: HomeScreenViewModel = koinInject()
    val isGeneratingPdf by vm.isGeneratingPdf.collectAsState()
    val pdfUrl by vm.pdfUrl.collectAsState()
    val pdfToastMessage by vm.pdfToastMessage.collectAsState()
    val pdfErrorToastMessage by vm.pdfErrorToastMessage.collectAsState()
    val fileDownloader = rememberFileDownloader()
    val actionsLoading by vm.isActionLoading.collectAsState()
    val pdfModuleType by vm.pdfModuleType.collectAsState()
    var actionErrorMessage: String? by remember { mutableStateOf(null) }
    var isRoleUpdated by remember { mutableStateOf(false) }

    
    val downloadingMsg = stringResource(Res.string.downloading_report, pdfModuleType ?: "")
    val failedMsg = stringResource(Res.string.failed_to_download_report, pdfModuleType ?: "")
    LaunchedEffect(pdfUrl) {
        pdfUrl?.let { url ->
            try {
                val fileName = "${pdfModuleType}_PDF_${Clock.System.now().toEpochMilliseconds()}.pdf"
                fileDownloader.downloadFile(url, fileName)
                vm.setPdfToastMessage(downloadingMsg)
            } catch (e: Exception) {
                vm.setPdfErrorToastMessage(failedMsg)
            }
            vm.clearPdfUrl()
        }
    }

    LaunchedEffect(Unit) {
        vm.userCheckRole { isUpdated ->
            isRoleUpdated = isUpdated
        }
    }


    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding()
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { pullDownRefresh() }
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(22.dp))
                HeaderView(
                    vm = viewModel,
                    onProfileClick = onProfileClick,
                    userName = vm.user?.name,
                    profileImage = vm.user?.profileImage,
                    notificationCount = vm.userInfo?.notificationUnReadCount,
                    onNotificationClick = onNotificationClick
                )
                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp)

                ) {
                    Spacer(modifier = Modifier.height(22.dp))
                    PendingActionsCardView(
                        actionOverview?.pendingActionsCount ?: 0,
                        onPendingActionViewAllClick
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    AssignedToMeCard(
                        assignedToMe = assignedToMe,
                        onViewAllClick = onPendingActionViewAllClick,
                        viewModel = vm,
                        onRefreshList = silentRefresh,
                        onPermitClick = onPermitClick,
                        onNavigateToProject = { groupId, groupCode ->
                            onNavigateToProject(groupId, groupCode)
                        },
                        onActionError = { err ->
                            actionErrorMessage = err
                        }
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                    ActionOverviewSection(
                        actionOverview = actionOverview,
                        onClickListener = { action ->
                            onClickModule(action)
                        }
                    )
                }
            }
        }


        AppErrorDialog(
            visible = isRoleUpdated,
            title = stringResource(Res.string.user_role_changed),
            description = stringResource(Res.string.user_role_changed_description),
            buttonText = stringResource(Res.string.logout),
            onDismiss = {
                isRoleUpdated = false
                viewModel.logout()
                onLogout()
            }
        )


        if (isGeneratingPdf) {
            PdfGenerationLoader()
        }

        if (actionsLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                AppLoader()
            }
        }

        ToastHost(
            modifier = Modifier.padding(horizontal = 22.dp),
            visible = pdfToastMessage != null || pdfErrorToastMessage != null,
            type = if (pdfErrorToastMessage != null) ToastType.Error else ToastType.Success,
            message = pdfErrorToastMessage ?: pdfToastMessage ?: "",
            onDismiss = {
                vm.clearToasts()
            }
        )

        ToastHost(
            modifier = Modifier.padding(horizontal = 22.dp),
            visible = actionErrorMessage != null ,
            type = ToastType.Error,
            message = actionErrorMessage ?: "",
            onDismiss = {
                actionErrorMessage = null
            }
        )
    }
}

@Composable
fun HeaderView(
    vm: ProfileViewModel,
    userName: String?,
    profileImage: String?,
    notificationCount: Int?,
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(Res.string.welcome_back),
                style = textStyle(
                    size = 14.sp,
                    weight = FontWeight.Normal
                ),
                color = AppColors.TextGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            userName?.let { name ->
                Text(
                    text = name,
                    style = textStyle(
                        size = 24.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.BlackText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Box(modifier = Modifier.clickable { onNotificationClick() }) {
            Image(
                painter = painterResource(Res.drawable.ic_bell),
                contentDescription = stringResource(Res.string.notifications),
                modifier = Modifier.size(28.dp)
            )

            if ((notificationCount ?: 0) > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 5.dp, y = (-8).dp)
                        .size(20.dp)
                        .background(Color.Red, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = notificationCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 9.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        WebImageView(
            imageUrl = profileImage,
            modifier = Modifier
                .size(49.dp)
                .clip(RoundedCornerShape(25))
                .clickable { onProfileClick() }
        )
    }
}

@Composable
fun PendingActionsCardView(
    pendingActionsCount: Int,
    onViewAllClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD42027),
                        Color(0xFFFCB922)
                    )
                )
            )
            .clickable {
                onViewAllClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 21.dp, vertical = 21.dp)
                .height(127.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(Res.string.pendingActions),
                    style = textStyle(
                        14.sp,
                        FontWeight.Normal
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        text = pendingActionsCount.toString(),
                        style = textStyle(
                            52.sp,
                            FontWeight.Normal
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = stringResource(Res.string.pending),
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = textStyle(
                            15.sp,
                            FontWeight.Medium
                        ),
                        color = Color.White
                    )
                }
            }

            Image(
                painter = painterResource(Res.drawable.ic_clock),
                contentDescription = null,
                modifier = Modifier.size(52.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignedToMeCard(
    assignedToMe: AssignedToMe?,
    viewModel: HomeScreenViewModel,
    onViewAllClick: () -> Unit = {},
    onRefreshList: () -> Unit = {},
    onPermitClick: (Int) -> Unit = {},
    onNavigateToProject: (Int, String) -> Unit = { _, _ -> },
    onActionError: (String) -> Unit
) {
    var showObservationDrawer by remember { mutableStateOf(false) }
    var showPermitDrawer by remember { mutableStateOf(false) }
    var selectedObservationId by remember { mutableStateOf<Int?>(null) }
    var closeObservationId by remember { mutableStateOf<Int?>(null) }
    var justification by remember { mutableStateOf<String?>(null) }
    var showRequestDeleteSheet by remember { mutableStateOf(false) }
    var showRequestResponsiblePersonSheet by remember { mutableStateOf(false) }
    val groupUsers by viewModel.groupUsers.collectAsState()
    var actionSuccessMessage by remember { mutableStateOf<String?>(null) }
    assignedToMe?.let { contents ->
        Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row() {
                Text(
                    stringResource(Res.string.assignedToMe),
                    style = textStyle(
                        14.sp,
                        FontWeight.Bold
                    )
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    stringResource(Res.string.viewAll),
                    modifier = Modifier.clickable { onViewAllClick() },
                    style = textStyle(
                        12.sp,
                        FontWeight.SemiBold,
                        color = AppColors.BlackText
                    )
                )
            }
            if (contents.observation != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showObservationDrawer = true }
                        .padding(vertical = 8.dp)
                ) {
                    if (contents.observation.imageUrl.isEmpty()) {
                        Image(
                            painter = painterResource(Res.drawable.ic_assigned_observation),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12))
                        )
                    } else {
                        WebImageView(
                            imageUrl = contents.observation.imageUrl,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12))
                        )
                    }
                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )
                    Column() {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusCard(
                                contents.observation.pendingActionType
                            )
                        }
                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DateCard(
                                formatDate(
                                    contents.observation.date,
                                    "yyyy-MM-dd HH:mm:ss",
                                    "dd MMM yyyy"
                                ).uppercase(),
                                timeAgo(
                                    contents.observation.date,
                                    isUtc = true
                                )
                            )
                        }
                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )
                        Text(
                            contents.observation.title,
                            style = textStyle(
                                14.sp,
                                FontWeight.Bold
                            )
                        )
                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )
                        contents.observation.reportedBy?.name?.takeIf { it.isNotEmpty() }?.let { name ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                WebImageView(
                                    imageUrl = contents.observation.reportedBy?.imageUrl.orEmpty(),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                )

                                Spacer(
                                    modifier = Modifier.width(10.dp)
                                )

                                Text(
                                    text = name,
                                    style = textStyle(
                                        11.sp,
                                        FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
                }
            }
            if (contents.permit != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPermitDrawer = true }
                        .padding(vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_assigned_permit),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12))
                    )
                    Spacer(
                        modifier = Modifier.width(14.dp)
                    )
                    Column() {
                        val status = PermitStatus.fromValue(contents.permit.status)
                        val statusTitle = status?.title ?: "UNKNOWN"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (status != null) Color(status.colorHex) else Color.Gray)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = statusTitle.uppercase(),
                                style = textStyle(size = 9.sp, weight = FontWeight.Bold),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DateCard(
                                formatDate(
                                    contents.permit.createdAt,
                                    "yyyy-MM-dd HH:mm:ss",
                                    "dd MMM yyyy"
                                ).uppercase(),
                                timeAgo(
                                    contents.permit.createdAt,
                                    isUtc = true
                                )
                            )
                        }
                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )
                        Text(
                            contents.permit.permitType?.title ?: "-",
                            style = textStyle(
                                14.sp,
                                FontWeight.Bold
                            )
                        )
                        Spacer(
                            modifier = Modifier.height(5.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Permit Code :",
                                style = textStyle(
                                    11.sp,
                                    FontWeight.Medium
                                )
                            )
                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )
                            Text(
                                contents.permit.permitCode,
                                style = textStyle(
                                    11.sp,
                                    FontWeight.Medium
                                )
                            )
                        }


                    }
                }
            }
        }
        ObservationActionBottomSheet(
            showSheet = showObservationDrawer,
            actionType = assignedToMe.observation?.pendingActionType ?: PendingActionStatusType.OPEN_OBSERVATION,
            onDismiss = {
                showObservationDrawer = false
            },
            onActionClick = { action ->
                println("action: $action")
                when (action) {
                    "Reject", "Reject Join", "Reject Change", "Reject Delete" -> {
                        viewModel.approveOrRejectPendingAction(
                            assignedToMe.observation?.pendingActionId ?: -1,
                            action = 2,
                            onSuccess = { msg ->
                                actionSuccessMessage = msg
                            },
                            onError = { err ->
                                onActionError(err)
                            }
                        )
                    }
                    "Approve", "Approve Join", "Approve Change", "Approve Delete" -> {
                        viewModel.approveOrRejectPendingAction(
                            assignedToMe.observation?.pendingActionId ?: -1,
                            action = 1,
                            onSuccess = { msg ->
                                actionSuccessMessage = msg
                            },
                            onError = { err ->
                                onActionError(err)
                            }
                        )
                    }
                    "View Project" -> {
                        val groupCode = assignedToMe.observation?.groupCode ?: ""
                        val groupId = groupCode.substringAfter("-").toIntOrNull()
                        onNavigateToProject(groupId ?: -1, assignedToMe.observation?.groupCode ?: "")
                    }
                    "View Report", "View Observation", "View Observation Closeout" -> {
                        selectedObservationId = assignedToMe.observation?.contentId
                    }
                    "Generate PDF" -> {
                        viewModel.generatePdf(assignedToMe.observation?.contentId ?: -1)
                    }
                    "Close Observation" -> {
                        closeObservationId = assignedToMe.observation?.contentId
                    }
                    "Request Observation Responsible Person Change" -> {
                        val obs = assignedToMe.observation
                        val groupCode = assignedToMe.observation?.groupCode
                        val groupId = groupCode?.substringAfter("-")?.toIntOrNull()
                        if (groupCode != null && groupId != null) {
                            viewModel.fetchGroupUsers(groupId, groupCode)
                        }
                        showRequestResponsiblePersonSheet = true
                    }
                    "Request to Delete Observation" -> {
                        showRequestDeleteSheet = true
                    }
                    "View Justification" -> {
                        justification = assignedToMe.observation?.justification
                    }
                }
            }
        )
        PermitActionBottomSheet(
            showSheet = showPermitDrawer,
            onDismiss = {
                showPermitDrawer = false
            },
            onActionClick = { action ->
                when (action) {
                    "View Permit" -> {
                        assignedToMe.permit?.permitId?.let { onPermitClick(it) }
                    }
                    "Generate PDF" -> {
                        assignedToMe.permit?.permitId?.let { viewModel.generatePermitPDF(it) }
                    }
                }
            }
        )

        if (selectedObservationId != null) {
            val obsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { selectedObservationId = null },
                sheetState = obsSheetState,
                containerColor = Color.White,
                dragHandle = null
            ) {
                Box(modifier = Modifier.fillMaxHeight(0.9f)) {
                    ObservationDetailScreen(
                        observationId = selectedObservationId ?: -1,
                        onBackClicked = { selectedObservationId = null },
                        onRefreshList = onRefreshList
                    )
                }
            }
        }

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
                        observationId = closeObservationId ?: -1,
                        onBackClicked = { closeObservationId = null },
                        onRefreshList = {
                            closeObservationId = null
                            onRefreshList()
                        },
                        startWithCloseForm = true
                    )
                }
            }
        }

        if (showRequestDeleteSheet) {
            val deleteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showRequestDeleteSheet = false },
                sheetState = deleteSheetState,
                containerColor = Color.White
            ) {
                RequestDeleteObservationView(
                    onBackClicked = { showRequestDeleteSheet = false },
                    onContinueClicked = { justification ->
                        val observationId = assignedToMe.observation?.contentId ?: return@RequestDeleteObservationView
                        viewModel.requestToDeleteObservation(
                            observationId = observationId,
                            justification = justification,
                            onSuccess = {
                                showRequestDeleteSheet = false
                                actionSuccessMessage = "Request to delete observation submitted successfully."
                            },
                            onError = { err ->
                                showRequestDeleteSheet = false
                                onActionError(err)
                            }
                        )
                    }
                )
            }
        }

        if (showRequestResponsiblePersonSheet) {
            val responsibleSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showRequestResponsiblePersonSheet = false },
                sheetState = responsibleSheetState,
                containerColor = Color.White
            ) {
                RequestResponsiblePersonChangeView(
                    users = groupUsers,
                    onBackClicked = { showRequestResponsiblePersonSheet = false },
                    onContinueClicked = { justification, responsiblePersonId ->
                        val observationId = assignedToMe.observation?.contentId ?: return@RequestResponsiblePersonChangeView
                        viewModel.requestResponsiblePersonChange(
                            observationId = observationId,
                            justification = justification,
                            responsiblePerson = responsiblePersonId,
                            onSuccess = {
                                showRequestResponsiblePersonSheet = false
                                actionSuccessMessage = "Request to change responsible person submitted successfully."
                            },
                            onError = { err ->
                                showRequestResponsiblePersonSheet = false
                                onActionError(err)
                            }
                        )
                    }
                )
            }
        }

        actionSuccessMessage?.let { msg ->
            AppStatusDialog(
                visible = true,
                title = "Success",
                description = msg,
                buttonText = "OK",
                onDismiss = {
                    actionSuccessMessage = null
                    onRefreshList()
                }
            )
        }

        justification?.let { msg ->
            AppStatusDialog(
                visible = true,
                title = "Justification",
                description = msg,
                buttonText = "OK",
                icon = Res.drawable.ic_justification,
                onDismiss = {
                    justification = null
                }
            )
        }
        } // end outer Box
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermitActionBottomSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onActionClick: (String) -> Unit
) {

    if (!showSheet) return

    val sheetState = rememberModalBottomSheetState()

    val actions = listOf(
        Pair("View Permit", stringResource(Res.string.viewPermit)),
        Pair("Generate PDF", stringResource(Res.string.generatePdf))
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {

            Text(
                text = stringResource(Res.string.permitToWork),
                style = textStyle(
                    size = 18.sp,
                    weight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            actions.forEach { (id, title) ->
                PermitActionItem(
                    title = title,
                    onClick = {
                        onActionClick(id)
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PermitActionItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = textStyle(
                size = 14.sp,
                weight = FontWeight.Medium
            )
        )

        Image(
            modifier = Modifier.padding(
                start = 40.dp
            )
                .rtlScale()
                .size(12.dp),
            painter = painterResource(Res.drawable.ic_right_icon),
            contentDescription = null,
        )
    }
}

@Composable
fun ActionOverviewSection(
    actionOverview: ActionsOverview?,
    onClickListener: (ActionOverview) -> Unit
) {
    val items = listOf(
        ActionOverviewItem(ActionOverview.AUDIT_INSPECTIONS, actionOverview?.auditAndInspectionsCount),
        ActionOverviewItem(ActionOverview.PERMIT_TO_WORK, actionOverview?.permitToWorkCount),
        ActionOverviewItem(ActionOverview.OBSERVATIONS, actionOverview?.observationsCount),
        ActionOverviewItem(ActionOverview.INCIDENTS, actionOverview?.incidentCount),
        ActionOverviewItem(ActionOverview.VIOLATIONS, actionOverview?.violationCount),
        ActionOverviewItem(ActionOverview.TRAINING, actionOverview?.trainingsCount)
    )
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            stringResource(Res.string.actionOverview),
            style = textStyle(
                14.sp,
                FontWeight.Bold
            )
        )
        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { item ->
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            ActionOverviewCard(
                                action = item.type,
                                count = item.count ?: 0,
                                onClick = { action ->
                                    onClickListener(action)
                                }
                            )
                        }
                    }

                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ActionOverviewCard(
    action: ActionOverview,
    count: Int,
    onClick: (ActionOverview) -> Unit
) {
    Box(
        modifier = Modifier
            .clickable {
                onClick(action)
            }
            .fillMaxWidth()
            .height(125.dp)
            .dropShadow(
                shape = RoundedCornerShape(16.dp),
                shadow = Shadow(
                    radius = 20.dp,
                    color = Color(0x0F000000),
                    offset = DpOffset(0.dp, 4.dp)
                )
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE5E5E5),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(action.icon),
                    contentDescription = stringResource(action.title),
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    text = count.toString().padStart(2, '0'),
                    style = textStyle(
                        24.sp,
                        FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(action.title),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = textStyle(
                    14.sp,
                    FontWeight.SemiBold,
                    lineHeight = 16.sp
                )
            )
        }
    }
}

@Composable
fun StatusCard(
    status: Int,
    modifier: Modifier = Modifier
) {
    val status = PendingActionType.fromValue(status)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(status.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(status.titleRes).uppercase(),
            style = textStyle(
                10.sp,
                FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = status.textColor,
            modifier = Modifier.padding(
                6.dp
            )
        )
    }
}

@Composable
fun DateCard(
    date: String,
    dateAgo: String
) {
Row(
    verticalAlignment = Alignment.CenterVertically
) {
    Image(
        painter = painterResource(Res.drawable.ic_calendar),
        contentDescription = null
    )
    Spacer(
        modifier = Modifier.width(4.dp)
    )
    Text(
        text = date,
        style = textStyle(
            10.sp,
            FontWeight.SemiBold
        )
    )
    Spacer(modifier = Modifier.weight(1f))
    Text(
        text = dateAgo,
        style = textStyle(
            10.sp,
            FontWeight.SemiBold
        )
    )

}
}

enum class HomeScreenStatus(
    val string: String,
    val bgColor: Color,
    val textColor: Color
) {
    PENDING(
        "Pending",
        AppColors.Primary,
        Color.White
    ),
    COMPLETED(
        "Completed",
        AppColors.DarkGray,
        Color.White
    )
}

enum class ActionOverview(
    val title: StringResource,
    val icon: DrawableResource,
    val count: Int = 0
) {
    AUDIT_INSPECTIONS(
        Res.string.audit_inspections,
        Res.drawable.ic_audit_inspection
    ),

    PERMIT_TO_WORK(
        Res.string.permit_to_work,
        Res.drawable.ic_permit_to_work
    ),

    OBSERVATIONS(
        Res.string.observations,
        Res.drawable.ic_observations
    ),

    INCIDENTS(
        Res.string.incidents,
        Res.drawable.ic_incidents
    ),

    VIOLATIONS(
        Res.string.violations,
        Res.drawable.ic_violations
    ),

    TRAINING(
        Res.string.training,
        Res.drawable.ic_training
    ),

    LESSONS_LEARNED(
        Res.string.lessons_learned,
        Res.drawable.ic_observations
    )
}

data class ActionOverviewItem(
    val type: ActionOverview,
    val count: Int?
)


enum class ObservationStatus(
    val id: Int,
    val title: StringResource,
    val backgroundColor: Color
) {
    OPEN(
        id = 1,
        title = Res.string.open,
        backgroundColor = Color(0xFFFA6345)
    ),
    CLOSED(
        id = 2,
        title = Res.string.close_out_pending,
        backgroundColor = Color(0xFFF6A03A)
    ),
    CLOSE_OUT_APPROVED(
        id = 3,
        title = Res.string.closed,
        backgroundColor = Color(0xFF45B743)
    );

    companion object {
        fun fromId(id: Int): ObservationStatus {
            return entries.find { it.id == id } ?: OPEN
        }
    }
}

enum class PendingActionType(
    val value: Int,
    val titleRes: StringResource
) {
    OPEN_OBSERVATION(1, Res.string.open),
    REQUEST_TO_JOIN_PROJECT(2, Res.string.requestToJoinProject),
    OBSERVATION_RESPONSIBILITY_CHANGE(3, Res.string.observationResponsibilityChange),
    REQUEST_TO_DELETE_OBSERVATION(4, Res.string.requestToDeleteObservation),
    REVIEW_OBSERVATION_CLOSE_OUT(5, Res.string.reviewObservationCloseout);

    companion object {
        fun fromValue(value: Int) =
            entries.firstOrNull { it.value == value } ?: OPEN_OBSERVATION
    }

    val textColor: Color
        get() = when (this) {
            OPEN_OBSERVATION -> AppColors.PendingStatusTeal
            REQUEST_TO_JOIN_PROJECT -> AppColors.PendingStatusSkyBlue
            OBSERVATION_RESPONSIBILITY_CHANGE -> AppColors.PendingStatusSalmon
            REQUEST_TO_DELETE_OBSERVATION -> AppColors.PendingStatusBlue
            REVIEW_OBSERVATION_CLOSE_OUT -> AppColors.PendingStatusOrange
        }

    val backgroundColor: Color
        get() = when (this) {
            OPEN_OBSERVATION -> AppColors.PendingStatusTealBackground
            REQUEST_TO_JOIN_PROJECT -> AppColors.PendingStatusSkyBlueBackground
            OBSERVATION_RESPONSIBILITY_CHANGE -> AppColors.PendingStatusSalmonBackground
            REQUEST_TO_DELETE_OBSERVATION -> AppColors.PendingStatusBlueBackground
            REVIEW_OBSERVATION_CLOSE_OUT -> AppColors.PendingStatusOrangeBackground
        }

    // colors...
}
