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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import instaresolv.shared.generated.resources.ic_violations
import instaresolv.shared.generated.resources.ic_training
import org.example.project.colors.AppColors
import org.example.project.homescreen.HomeScreenViewModel
import org.example.project.profile.ProfileViewModel
import org.example.project.typography.textStyle
import org.example.project.ui.components.WebImageView
import org.koin.compose.koinInject

@Composable
fun HomeScreenContentView(
    onProfileClick: () -> Unit
) {
    val viewModel: ProfileViewModel = koinInject()
    val vm: HomeScreenViewModel = koinInject()
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding()
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
                onNotificationClick = {  }
            )
            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)

            ) {
                Spacer(modifier = Modifier.height(22.dp))
                PendingActionsCardView(
                    0
                )
                Spacer(modifier = Modifier.height(26.dp))
                AssignedToMeCard()
                Spacer(modifier = Modifier.height(22.dp))
                ActionOverviewSection()
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
        Box {
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

@Composable
fun AssignedToMeCard() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row() {
            Text(
                stringResource(Res.string.assigned_to_me),
                style = textStyle(
                    14.sp,
                    FontWeight.Bold
                )
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Text(
                stringResource(Res.string.view_all),
                style = textStyle(
                    12.sp,
                    FontWeight.SemiBold,
                    color = AppColors.BlackText
                )
            )
        }
        Row() {
            Image(
                painter = painterResource(Res.drawable.ic_pipe),
                contentDescription = null
            )
            Spacer(
                modifier = Modifier.width(14.dp)
            )
            Column() {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusCard(
                        HomeScreenStatus.PENDING
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    DateCard(
                        "18 AUG 2025",
                        "8 Months Ago"
                    )
                }
                Spacer(
                    modifier = Modifier.height(10.dp)
                )
                Text(
                    "Water pipe broken in the 2nd floor bathroom",
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
                    Image(
                        painter = painterResource(Res.drawable.ic_user),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                    )
                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )
                    Text(
                        "Cristofer Press",
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

@Composable
fun ActionOverviewSection() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            stringResource(Res.string.action_overview),
            style = textStyle(
                14.sp,
                FontWeight.Bold
            )
        )
        Spacer(
            modifier = Modifier.height(10.dp)
        )

        ActionOverview.entries.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { action ->
                    Box(modifier = Modifier.weight(1f)) {
                        ActionOverviewCard(
                            action,
                            0
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ActionOverviewCard(
    action: ActionOverview,
    count: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(113.dp)
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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = action.title,
                style = textStyle(
                    14.sp,
                    FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun StatusCard(
    status: HomeScreenStatus,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(status.bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.string.uppercase(),
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
    val title: String,
    val icon: DrawableResource
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
