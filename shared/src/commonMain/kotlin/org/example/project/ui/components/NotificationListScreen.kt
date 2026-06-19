package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_calendar
import instaresolv.shared.generated.resources.ic_lesson
import org.example.project.colors.AppColors
import org.example.project.data.model.NotificationListModel
import org.example.project.data.model.NotificationListResponse
import org.example.project.data.settings.formatDate
import org.example.project.data.settings.timeAgo
import org.example.project.notifications.NotificationUiState
import org.example.project.notifications.NotificationsViewModel
import org.example.project.typography.textStyle
import org.example.project.utilites.NavigationBackIcon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun NotificationListScreen(
    onBackClicked: () -> Unit
) {
    val viewModel: NotificationsViewModel = koinInject()
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
//                    .padding(horizontal = 25.dp)
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(
                    onBackClicked
                )
                Text(
                    text = "Notifications".uppercase(),
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
                    LazyColumn {
                        items((uiState.value as NotificationUiState.Success).response.notifications.size) {
                            NotificationListRow((uiState.value as NotificationUiState.Success).response.notifications[it])
                        }
                    }
                }
                is NotificationUiState.Error -> {

                }
            }
        }
    }

}

@Composable
fun NotificationListRow(
    notification: NotificationListModel
) {
    val dateString = notification.date ?: ""
    val cleanDate = if (dateString.contains("T")) {
        dateString.substringBefore(".").replace("T", " ").replace("Z", "")
    } else dateString

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (!notification.isRead) Color(0xFFF5F5F5) else Color.Transparent)
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
                    if (cleanDate.isNotEmpty()) timeAgo(cleanDate) else "",
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