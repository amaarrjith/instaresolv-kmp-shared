
package org.example.project.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_lock
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.login
import org.example.project.colors.AppColors
import org.example.project.data.model.Project
import org.example.project.project.ProjectListUiState
import org.example.project.project.ProjectViewModel
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppSearchBar
import org.example.project.utilites.NavigationBackIcon
import org.koin.compose.koinInject

@Composable
fun ProjectListScreen() {
    var searchQuery by remember { mutableStateOf("") }
    val viewModel: ProjectViewModel = koinInject()
    val uiState = viewModel.uiState.collectAsState()
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ProjectListScreenTopBar()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 28.dp)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                AppSearchBar(
                    value = searchQuery,
                    onValueChange = {
                        it -> searchQuery
                        viewModel.getProjects(searchQuery)
                                    },
                    placeholder = "Search Projects",
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.height(20.dp))
                when(uiState.value) {
                    is ProjectListUiState.Loading -> {
                        AppLoader()
                    }
                    is ProjectListUiState.Error -> {

                    }
                    is ProjectListUiState.Success -> {
                        ProjectListScreenView(
                            uiState = uiState.value as ProjectListUiState.Success
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectListScreenView(
    uiState: ProjectListUiState.Success
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            items(uiState.projectList.size) { item ->
                ProjectListCard(project = uiState.projectList[item])
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ProjectListScreenTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "PROJECTS",
            style = textStyle(
                size = 14.sp,
                weight = FontWeight.Bold
            ),
            color = AppColors.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        RequestButton()
        Spacer(modifier = Modifier.width(8.dp))
        CreateButton()
    }
}

@Composable
fun RequestButton() {
    Box(
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.SkyBlue.copy(alpha = 0.1f))
                .padding(horizontal = 14.dp)
                .padding(vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_lock),
                contentDescription = null
            )
            Text(
                text = "Request",
                style = textStyle(
                    size = 13.sp,
                    weight = FontWeight.Bold
                ),
                color = AppColors.SkyBlue
            )
        }
    }
}

@Composable
fun CreateButton() {
    Box(
//        modifier = Modifier.padding(10.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.Primary.copy(alpha = 0.1f))
                .padding(horizontal = 14.dp)
                .padding(vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_add),
                contentDescription = null
            )
            Text(
                text = "Create",
                style = textStyle(
                    size = 13.sp,
                    weight = FontWeight.Bold
                ),
                color = AppColors.Primary
            )
        }
    }
}

@Composable
fun ProjectListCard(
    modifier: Modifier = Modifier,
    project: Project
) {
    Row(
        modifier = modifier
            .padding(vertical = 13.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WebImageView(
            imageUrl = project.groupImage,
            modifier = Modifier
                .size(69.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.width(15.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = project.groupName ?: "",
                style = textStyle(
                    size = 14.sp,
                    weight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(9.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray)
            ) {
                Text(
                    text = project.groupCode ?: "",
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 5.dp),
                    style = textStyle(
                        size = 10.sp,
                        weight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
            }
        }

    }
}

fun Modifier.shimmer(): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE0E0E0),
            Color(0xFFF5F5F5),
            Color(0xFFE0E0E0)
        ),
        start = Offset(translateAnim - 200f, 0f),
        end = Offset(translateAnim, 0f)
    )
    this.background(brush)
}