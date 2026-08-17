package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.close
import instaresolv.shared.generated.resources.ic_rejected
import instaresolv.shared.generated.resources.ic_sucess_logo
import org.example.project.colors.AppColors
import org.example.project.data.model.NotificationListModel
import org.example.project.data.model.Project
import org.example.project.project.ProjectListUiState
import org.example.project.project.ProjectViewModel
import org.example.project.typography.textStyle
import org.example.project.utilites.AppPrimaryButton
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ProjectRequestPopUpView(
    visible: Boolean,
    notification: NotificationListModel,
    buttonText: String = "Okay",
    icon: DrawableResource = Res.drawable.ic_rejected,
    viewModel: ProjectViewModel = koinInject(),
    onDismiss: () -> Unit
) {
    var project by remember { mutableStateOf<Project?>(null) }
    val uiState = viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        val parts = notification.groupCode?.split("-")
        val groupId = parts?.getOrNull(0) ?: "-1"
        val groupCode = parts?.getOrNull(1) ?: "-1"
        viewModel.viewProject(
            groupId,
            groupCode,
            onSuccess = { data ->
                project = data
            },
            onError = {}
        )
    }
    if (visible) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(
                    modifier = Modifier.padding(top = 40.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(75.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = notification.title ?: "Rejected",
                        style = textStyle(size = 20.sp, weight = FontWeight.Bold),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    if (notification.description?.isNotBlank() == true) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = notification.description,
                            style = textStyle(size = 14.sp, weight = FontWeight.Normal),
                            color = Color(0xFF6B7280), // Gray text
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    when(uiState.value) {
                        is ProjectListUiState.Error -> {

                        }
                        ProjectListUiState.Loading -> {
                            AppLoader()
                        }
                        is ProjectListUiState.Success -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        WebImageView(
                                            imageUrl = project?.groupImage
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = project?.groupName ?: "",
                                            style = textStyle(size = 15.sp, weight = FontWeight.Bold),
                                            color = AppColors.Black
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color.Gray)
                                            ) {
                                                Text(
                                                    text = project?.groupCode ?: "",
                                                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 6.dp),
                                                    style = textStyle(size = 10.sp, weight = FontWeight.SemiBold),
                                                    color = Color.White
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                project?.description?.let { desc ->
                                    Text(
                                        text = desc,
                                        style = textStyle(size = 13.sp, weight = FontWeight.Normal),
                                        color = Color.DarkGray,
                                        lineHeight = 20.sp,
                                        maxLines = 4,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(30.dp))
                                }
                            }
//                            Spacer(modifier = Modifier.height(32.dp))
                        }
                        is ProjectListUiState.ViewProject -> {

                        }
                    }
                    AppPrimaryButton(
                        title = buttonText,
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}