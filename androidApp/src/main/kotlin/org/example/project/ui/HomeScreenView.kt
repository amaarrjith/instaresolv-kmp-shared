package org.example.project.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.R
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle

@Composable
fun HomeScreenContentView() {
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .padding()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(22.dp))
            HeaderView()
            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(26.dp))
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
    userName: String = "Amarjith",
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

            Text(
                text = userName,
                style = textStyle(
                    size = 24.sp,
                    weight = FontWeight.Bold
                ),
                color = AppColors.BlackText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_bell),
            contentDescription = "Notifications"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = painterResource(R.drawable.ic_user),
            contentDescription = "Profile"
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
            .height(127.dp)
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
                .padding(horizontal = 21.dp, vertical = 21.dp),
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
                    verticalAlignment = Alignment.Bottom
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
                painter = painterResource(R.drawable.ic_clock),
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
                stringResource(R.string.assigned_to_me),
                style = textStyle(
                    14.sp,
                    FontWeight.Bold
                )
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Text(
                stringResource(R.string.view_all),
                style = textStyle(
                    12.sp,
                    FontWeight.SemiBold,
                    color = AppColors.BlackText
                )
            )
        }
        Row() {
            Image(
                painter = painterResource(R.drawable.ic_pipe),
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
                        painter = painterResource(R.drawable.ic_user),
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
            stringResource(R.string.action_overview),
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
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = action.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
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
        painter = painterResource(R.drawable.ic_calendar),
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

@Preview
@Composable
fun HomeScreenContentViewPreview() {
    HomeScreenContentView()
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
    val icon: Int
) {
    AUDIT_INSPECTIONS(
        "Audit & Inspections",
        R.drawable.ic_audit_inspection
    ),

    PERMIT_TO_WORK(
        "Permit to Work",
        R.drawable.ic_permit_to_work
    ),

    OBSERVATIONS(
        "Observations",
        R.drawable.ic_observations
    ),

    INCIDENTS(
        "Incidents",
        R.drawable.ic_incidents
    ),

    VIOLATIONS(
        "Violations",
        R.drawable.ic_violations
    ),

    TRAINING(
        "Training",
        R.drawable.ic_training
    )
}