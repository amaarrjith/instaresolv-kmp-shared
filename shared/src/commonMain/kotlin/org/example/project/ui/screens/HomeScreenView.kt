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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import org.jetbrains.compose.resources.painterResource
import org.example.project.localization.LocalAppStrings
import androidx.compose.ui.text.font.FontWeight
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
import instaresolv.shared.generated.resources.ic_right_icon
import instaresolv.shared.generated.resources.ic_violations
import instaresolv.shared.generated.resources.ic_training
import org.example.project.colors.AppColors
import org.example.project.data.model.ActionsOverview
import org.example.project.data.model.AssignedToMe
import org.example.project.data.settings.timeAgo
import org.example.project.homescreen.HomeScreenViewModel
import org.example.project.profile.ProfileViewModel
import org.example.project.typography.textStyle
import org.example.project.ui.components.WebImageView
import org.koin.compose.koinInject

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
    onPendingActionViewAllClick: () -> Unit = {}
) {

    val viewModel: ProfileViewModel = koinInject()
    val vm: HomeScreenViewModel = koinInject()
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
                        actionOverview?.pendingActionsCount ?: 0
                    )
                    Spacer(modifier = Modifier.height(26.dp))
                    AssignedToMeCard(
                        assignedToMe = assignedToMe,
                        onViewAllClick = onPendingActionViewAllClick
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
                text = "Hi, Welcome Back",
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
                contentDescription = "Notifications"
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
    pendingActionsCount: Int
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
                    text = "Pending Actions",
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
                        text = "Pending",
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
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignedToMeCard(
    assignedToMe: AssignedToMe?,
    onViewAllClick: () -> Unit = {}
) {
    var showDrawer by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val strings = LocalAppStrings.current
    assignedToMe?.let { contents ->
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row() {
                Text(
                    strings.assignedToMe,
                    style = textStyle(
                        14.sp,
                        FontWeight.Bold
                    )
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    strings.viewAll,
                    modifier = Modifier.clickable { onViewAllClick() },
                    style = textStyle(
                        12.sp,
                        FontWeight.SemiBold,
                        color = AppColors.BlackText
                    )
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDrawer = true }
                    .padding(vertical = 8.dp)
            ) {
                WebImageView(
                    imageUrl = contents.observation.imageUrl,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(12))
                )
                Spacer(
                    modifier = Modifier.width(14.dp)
                )
                Column() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusCard(
                            contents.observation.pendingActionType
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        DateCard(
                            formatDate(
                                contents.observation.date,
                                "yyyy-MM-dd HH:mm:ss",
                                "dd MMM yyyy"
                            ).uppercase(),
                            timeAgo(
                                contents.observation.date
                            )
                        )
                    }
                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                    Text(
                        contents.observation.title,
                        style = textStyle(
                            14.sp,
                            FontWeight.Bold
                        )
                    )
                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WebImageView(
                            imageUrl = contents.observation.reportedBy.imageUrl,
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )
                        Text(
                            contents.observation.reportedBy.name,
                            style = textStyle(
                                11.sp,
                                FontWeight.Medium
                            )
                        )
                    }


                }
            }
        }
        ObservationActionBottomSheet(
            showSheet = showDrawer,
            onDismiss = {
                showDrawer = false
            },
            onActionClick = { action ->
                when (action) {
                    "View Report" -> {}
                    "Generate PDF" -> {}
                    "Close Observation" -> {}
                    "Request Observation Responsible Person Change" -> {}
                    "Request to Delete Observation" -> {}
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservationActionBottomSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onActionClick: (String) -> Unit
) {

    if (!showSheet) return

    val sheetState = rememberModalBottomSheetState()

    val actions = listOf(
        "View Report",
        "Generate PDF",
        "Close Observation",
        "Request Observation Responsible Person Change",
        "Request to Delete Observation"
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
                text = "Open Observation",
                style = textStyle(
                    size = 18.sp,
                    weight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            actions.forEachIndexed { index, title ->
                ObservationActionItem(
                    title = title,
                    onClick = {
                        onActionClick(title)
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ObservationActionItem(
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
            ),
            painter = painterResource(Res.drawable.ic_right_icon),
            contentDescription = null
        )
    }
}

@Composable
fun ActionOverviewSection(
    actionOverview: ActionsOverview?,
    onClickListener: (ActionOverview) -> Unit
) {
    val strings = LocalAppStrings.current
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
            strings.actionOverview,
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
                    contentDescription = action.title
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
                text = action.title,
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
    val status = ObservationStatus.fromId(status)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(status.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.title.uppercase(),
            style = textStyle(
                10.sp,
                FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            ),
            color = Color.White,
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
    val title: String,
    val icon: DrawableResource,
    val count: Int = 0
) {
    AUDIT_INSPECTIONS(
        "Audit & Inspections",
        Res.drawable.ic_audit_inspection
    ),

    PERMIT_TO_WORK(
        "Permit to Work",
        Res.drawable.ic_permit_to_work
    ),

    OBSERVATIONS(
        "Observations",
        Res.drawable.ic_observations
    ),

    INCIDENTS(
        "Incidents",
        Res.drawable.ic_incidents
    ),

    VIOLATIONS(
        "Violations",
        Res.drawable.ic_violations
    ),

    TRAINING(
        "Training",
        Res.drawable.ic_training
    )
}

data class ActionOverviewItem(
    val type: ActionOverview,
    val count: Int?
)


enum class ObservationStatus(
    val id: Int,
    val title: String,
    val backgroundColor: Color
) {
    OPEN(
        id = 1,
        title = "Open",
        backgroundColor = Color(0xFFFA6345)
    ),
    CLOSED(
        id = 2,
        title = "Closed",
        backgroundColor = Color(0xFF45B743)
    ),
    CLOSE_OUT_APPROVED(
        id = 3,
        title = "Close Out Approved",
        backgroundColor = Color(0xFFF6A03A)
    );

    companion object {
        fun fromId(id: Int): ObservationStatus {
            return entries.find { it.id == id } ?: OPEN
        }
    }
}