package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_calendar
import instaresolv.shared.generated.resources.notifications
import org.example.project.colors.AppColors
import org.example.project.data.model.NotificationListModel
import org.example.project.data.settings.formatDate
import org.example.project.data.settings.timeAgo
import org.example.project.navigation.Screens
import org.example.project.notifications.NotificationUiState
import org.example.project.notifications.NotificationsViewModel
import org.example.project.typography.textStyle
import org.example.project.ui.screens.EmptyScreenView
import org.example.project.ui.screens.ObservationDetailScreen
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.NavigationBackIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

fun isObservationNotification(type: Int): Boolean {
    val notificationType = AppNotificationType.fromValue(type) ?: return false
    return when (notificationType) {
        AppNotificationType.OBSERVATION_CREATED,
        AppNotificationType.OBSERVATION_CREATED_NF,
        AppNotificationType.OBSERVATION_CREATED_TO_RESPONSIBLE_PERSON,
        AppNotificationType.OBSERVATION_CREATED_TO_RESPONSIBLE_PERSON_NF,
        AppNotificationType.OBSERVATION_CLOSED_TO_RESPONSIBLE_PERSON,
        AppNotificationType.OBSERVATION_CLOSED_TO_RESPONSIBLE_PERSON_NF,
        AppNotificationType.REVIEW_OBSERVATION_CLOSE_OUT_APPROVED,
        AppNotificationType.REVIEW_OBSERVATION_CLOSE_OUT_APPROVED_NF,
        AppNotificationType.REVIEW_OBSERVATION_CLOSE_OUT_REJECTED,
        AppNotificationType.REVIEW_OBSERVATION_CLOSE_OUT_REJECTED_NF,
        AppNotificationType.REASSIGN_RESPONSIBLE_PERSON_APPROVED,
        AppNotificationType.REASSIGN_RESPONSIBLE_PERSON_APPROVED_NF,
        AppNotificationType.REASSIGN_RESPONSIBLE_PERSON_REJECTED,
        AppNotificationType.REASSIGN_RESPONSIBLE_PERSON_REJECTED_NF,
        AppNotificationType.DELETE_OBSERVATION_REQUEST_APPROVED,
        AppNotificationType.DELETE_OBSERVATION_REQUEST_APPROVED_NF,
        AppNotificationType.DELETE_OBSERVATION_REQUEST_REJECTED,
        AppNotificationType.DELETE_OBSERVATION_REQUEST_REJECTED_NF -> true
        else -> false
    }
}

fun isPopupNotification(type: Int): Boolean {
    val notificationType = AppNotificationType.fromValue(type) ?: return false
    return when (notificationType) {
        AppNotificationType.REMOVE_FROM_GROUP,
        AppNotificationType.REMOVE_FROM_GROUP_NF,
        AppNotificationType.JOIN_GROUP_REQUEST_REJECTED,
        AppNotificationType.JOIN_GROUP_REQUEST_REJECTED_NF -> true
        else -> false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(
    onBackClicked: () -> Unit,
    onNotificationClick: ((NotificationListModel) -> Unit)? = null
) {
    val viewModel: NotificationsViewModel = koinInject()
    val uiState = viewModel.uiState.collectAsState()
    var selectedObservationId by remember { mutableStateOf<Int?>(null) }
    var selectedPopupNotification by remember { mutableStateOf<NotificationListModel?>(null) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(
                    onBackClicked
                )
                Text(
                    text = stringResource(Res.string.notifications).uppercase(),
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
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when(uiState.value) {
                is NotificationUiState.Loading -> {
                    AppLoader()
                }
                is NotificationUiState.Success -> {
                    val notifications = (uiState.value as NotificationUiState.Success).response.notifications
                    if (notifications.isEmpty()) {
                        EmptyScreenView(
                            message = "You have no notifications yet"
                        )
                    } else {
                        LazyColumn {
                            items(notifications.size) { index ->
                                val notification = notifications[index]
                                NotificationListRow(
                                    notification = notification,
                                    onClick = {
                                        if (isObservationNotification(notification.type) && notification.contentId > 0) {
                                            selectedObservationId = notification.contentId
                                        } else if (isPopupNotification(notification.type)) {
                                            selectedPopupNotification = notification
                                        } else {
                                            onNotificationClick?.invoke(notification)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                is NotificationUiState.Error -> {
                    ErrorRetryView(
                        (uiState.value as NotificationUiState.Error).errorMessage,
                        modifier = Modifier.fillMaxSize(),
                        onRetryClick = {
                            viewModel.getNotifications()
                        }
                    )
                }
            }

            if (selectedObservationId != null) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = { selectedObservationId = null },
                    sheetState = sheetState,
                    containerColor = Color.White,
                    dragHandle = null
                ) {
                    Box(modifier = Modifier.fillMaxHeight(0.9f)) {
                        ObservationDetailScreen(
                            observationId = selectedObservationId!!,
                            onBackClicked = { selectedObservationId = null },
                            onRefreshList = {
                                viewModel.getNotifications()
                            }
                        )
                    }
                }
            }

            selectedPopupNotification?.let {
                ProjectRequestPopUpView(
                    visible = true,
                    notification = it,
                    buttonText = "Close",
                    onDismiss = { selectedPopupNotification = null }
                )
            }
        }
    }
}

@Composable
fun NotificationListRow(
    notification: NotificationListModel,
    onClick: () -> Unit = {}
) {
    val dateString = notification.date ?: ""
    val cleanDate = if (dateString.contains("T")) {
        dateString.substringBefore(".").replace("T", " ").replace("Z", "")
    } else dateString

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (!notification.isRead) Color(0xFFF5F5F5) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 25.dp)
            .padding(vertical = 10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row() {
                Text(
                    notification.title ?: "",
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.SemiBold
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    if (cleanDate.isNotEmpty()) timeAgo(cleanDate, isUtc = true) else "",
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.Normal
                    )
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_calendar),
                    contentDescription = null,
                    tint = AppColors.Primary
                )

                Text(
                    if (cleanDate.isNotEmpty()) {
                        formatDate(
                            cleanDate,
                            "yyyy-MM-dd HH:mm:ss",
                            "dd MMM yyyy"
                        ).uppercase()
                    } else "",
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.Normal
                    )
                )
                Text(
                    notification.groupCode ?: "",
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.Normal
                    )
                )
            }
            Text(
                notification.description ?: "",
                style = textStyle(
                    size = 12.sp,
                    weight = FontWeight.Normal
                )
            )
            HorizontalDivider()
        }
    }
}

@Composable
@Preview
fun NotificationListScreenPreview() {
    NotificationListScreen(
        onBackClicked = {}
    )
}

enum class AppNotificationType(val value: Int) {
    JOIN_GROUP_REQUEST_ACCEPTED(1),
    JOIN_GROUP_REQUEST_REJECTED(2),
    ADDED_TO_GROUP(3),
    REMOVE_FROM_GROUP(4),
    DELETE_OBSERVATION_REQUEST_APPROVED(5),
    DELETE_OBSERVATION_REQUEST_REJECTED(6),
    OBSERVATION_DELETED(7),
    REVIEW_OBSERVATION_CLOSE_OUT_APPROVED(8),
    REVIEW_OBSERVATION_CLOSE_OUT_REJECTED(9),
    REASSIGN_RESPONSIBLE_PERSON_APPROVED(10),
    REASSIGN_RESPONSIBLE_PERSON_REJECTED(11),
    GROUP_MEMBER_ROLE_CHANGED(12),
    OBSERVATION_CREATED(13),
    GENERAL(14),
    OBSERVATION_CREATED_TO_RESPONSIBLE_PERSON(15),
    OBSERVATION_CLOSED_TO_RESPONSIBLE_PERSON(16),

    // New Flow
    JOIN_GROUP_REQUEST_ACCEPTED_NF(17),
    JOIN_GROUP_REQUEST_REJECTED_NF(18),
    ADDED_TO_GROUP_NF(19),
    REMOVE_FROM_GROUP_NF(20),
    DELETE_OBSERVATION_REQUEST_APPROVED_NF(21),
    DELETE_OBSERVATION_REQUEST_REJECTED_NF(22),
    OBSERVATION_DELETED_NF(23),
    REVIEW_OBSERVATION_CLOSE_OUT_APPROVED_NF(24),
    REVIEW_OBSERVATION_CLOSE_OUT_REJECTED_NF(25),
    REASSIGN_RESPONSIBLE_PERSON_APPROVED_NF(26),
    REASSIGN_RESPONSIBLE_PERSON_REJECTED_NF(27),
    GROUP_MEMBER_ROLE_CHANGED_NF(28),
    OBSERVATION_CREATED_NF(29),
    GENERAL_NF(30),
    OBSERVATION_CREATED_TO_RESPONSIBLE_PERSON_NF(31),
    OBSERVATION_CLOSED_TO_RESPONSIBLE_PERSON_NF(32),

    PRE_TASK_BRIEFING(33),
    PERMIT(34),
    TRAINING(35);

    companion object {
        fun fromValue(value: Int): AppNotificationType? {
            return entries.find { it.value == value }
        }
    }
}

fun AppNotificationClickListner(
    contentId: Int,
    type: Int,
    navController: NavController? = null,
    onNavigate: ((String) -> Unit)? = null
) {
    AppNotificationClickListener(
        notification = NotificationListModel(
            id = 0,
            type = type,
            contentId = contentId,
            title = null,
            time = null,
            date = null,
            description = null,
            groupCode = null,
            isRead = true
        ),
        navController = navController,
        onNavigate = onNavigate,
        onShowPopUp = {

        }
    )
}

fun AppNotificationClickListener(
    notification: NotificationListModel,
    navController: NavController? = null,
    onNavigate: ((String) -> Unit)? = null,
    onShowPopUp: ((NotificationListModel) -> Unit)? = null
) {
    if (notification.pushType == 1) {
        val notificationType = AppNotificationType.fromValue(notification.type)
        val route = when (notificationType) {
            AppNotificationType.OBSERVATION_CREATED,
            AppNotificationType.OBSERVATION_CREATED_NF,
            AppNotificationType.OBSERVATION_CREATED_TO_RESPONSIBLE_PERSON,
            AppNotificationType.OBSERVATION_CREATED_TO_RESPONSIBLE_PERSON_NF,
            AppNotificationType.OBSERVATION_CLOSED_TO_RESPONSIBLE_PERSON,
            AppNotificationType.OBSERVATION_CLOSED_TO_RESPONSIBLE_PERSON_NF,
            AppNotificationType.REVIEW_OBSERVATION_CLOSE_OUT_APPROVED,
            AppNotificationType.REVIEW_OBSERVATION_CLOSE_OUT_APPROVED_NF,
            AppNotificationType.REVIEW_OBSERVATION_CLOSE_OUT_REJECTED,
            AppNotificationType.REVIEW_OBSERVATION_CLOSE_OUT_REJECTED_NF,
            AppNotificationType.REASSIGN_RESPONSIBLE_PERSON_APPROVED,
            AppNotificationType.REASSIGN_RESPONSIBLE_PERSON_APPROVED_NF,
            AppNotificationType.REASSIGN_RESPONSIBLE_PERSON_REJECTED,
            AppNotificationType.REASSIGN_RESPONSIBLE_PERSON_REJECTED_NF,
            AppNotificationType.DELETE_OBSERVATION_REQUEST_APPROVED,
            AppNotificationType.DELETE_OBSERVATION_REQUEST_APPROVED_NF,
            AppNotificationType.DELETE_OBSERVATION_REQUEST_REJECTED,
            AppNotificationType.DELETE_OBSERVATION_REQUEST_REJECTED_NF -> {
                if (notification.contentId > 0) {
                    "${Screens.ObservationDetailsScreen.route}/${notification.contentId}"
                } else {
                    Screens.ObservationListScreen.route
                }
            }

            AppNotificationType.OBSERVATION_DELETED,
            AppNotificationType.OBSERVATION_DELETED_NF -> {
                Screens.ObservationListScreen.route
            }

            AppNotificationType.JOIN_GROUP_REQUEST_ACCEPTED,
            AppNotificationType.JOIN_GROUP_REQUEST_ACCEPTED_NF,
            AppNotificationType.ADDED_TO_GROUP,
            AppNotificationType.ADDED_TO_GROUP_NF,
            AppNotificationType.GROUP_MEMBER_ROLE_CHANGED,
            AppNotificationType.GROUP_MEMBER_ROLE_CHANGED_NF -> {
                val parts = notification.groupCode?.split("-")
                val groupId = parts?.getOrNull(1) ?: "-1"
                val groupCode = notification.groupCode ?: "-1"
                "${Screens.ProjectDetailScreen.route}/$groupId/$groupCode"
            }

            AppNotificationType.REMOVE_FROM_GROUP,
            AppNotificationType.REMOVE_FROM_GROUP_NF,
            AppNotificationType.JOIN_GROUP_REQUEST_REJECTED,
            AppNotificationType.JOIN_GROUP_REQUEST_REJECTED_NF -> {
                onShowPopUp?.invoke(notification)
                null
            }

            AppNotificationType.PRE_TASK_BRIEFING -> {
                Screens.PreTaskListScreen.route
            }

            AppNotificationType.PERMIT -> {
                if (notification.contentId > 0) {
                    "${Screens.PermitDetailScreen.route}/${notification.contentId}"
                } else {
                    Screens.PermitToWorkListScreen.route
                }
            }

            AppNotificationType.TRAINING -> {
                if (notification.contentId > 0) {
                    "${Screens.TrainingDetailScreen.route}/${notification.contentId}"
                } else {
                    Screens.TrainingListScreen.route
                }
            }

            AppNotificationType.GENERAL,
            AppNotificationType.GENERAL_NF,
            null -> null
        }

        route?.let { destinationRoute ->
            if (onNavigate != null) {
                onNavigate(destinationRoute)
            } else {
                navController?.navigate(destinationRoute)
            }
        }
    } else {
        navController?.navigate(Screens.PendingActionListScreen.route)
    }
}