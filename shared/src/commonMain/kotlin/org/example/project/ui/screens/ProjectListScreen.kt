
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
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
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
import instaresolv.shared.generated.resources.ic_key
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
fun ProjectListScreen(
    onCreateClicked: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val viewModel: ProjectViewModel = koinInject()
    val uiState = viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    var showRequestModal by remember { mutableStateOf(false) }
    var showViewProjectModal by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var toastType by remember { mutableStateOf(ToastType.Success) }
    var viewedProject by remember { mutableStateOf<Project?>(null) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            ProjectListScreenTopBar(
                onCreateClicked = { onCreateClicked() },
                onRequestClicked = { showRequestModal = true }
            )
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
                        searchQuery = it
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
                            uiState = uiState.value as ProjectListUiState.Success,
                            onRefresh = {   viewModel.getProjects(searchKey = searchQuery, isRefresh = true)  },
                            isRefreshing = isRefreshing
                        )
                    }
                    else -> {}
                }
            }

            ToastHost(
                visible = toastMessage != null,
                message = toastMessage ?: "",
                onDismiss = { toastMessage = null },
                type = toastType
            )

            RequestProjectBottomSheet(
                showSheet = showRequestModal,
                onDismiss = { showRequestModal = false },
                onContinue = { part1, part2 ->
                    viewModel.requestProjectAccess(
                        part1 = part1,
                        part2 = part2,
                        onSuccess = {
                            showRequestModal = false
                            toastMessage = "Project Request Successful"
                            toastType = ToastType.Success
                        },
                        onError = { message ->
                            toastMessage = message
                            toastType = ToastType.Error
                        }
                    )
                },
                onViewProject = { part1, part2 ->
                    viewModel.viewProject(
                        part1 = part1,
                        part2 = part2,
                        onSuccess = { project ->
                            showRequestModal = false
                            viewedProject = project
                            showViewProjectModal = true
                        },
                        onError = { message ->
                            toastMessage = message
                            toastType = ToastType.Error
                        }
                    )
                },
                toastMessage = toastMessage,
                toastType = toastType,
                onClearToast = { toastMessage = null }
            )

            ViewProjectDialog(
                showDialog = showViewProjectModal,
                onDismiss = { showViewProjectModal = false },
                project = viewedProject
            )
        }
    }
}

@Composable
fun ProjectListScreenView(
    onRefresh: () -> Unit,
    uiState: ProjectListUiState.Success,
    isRefreshing: Boolean = false
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            onRefresh()
        }
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
}

@Composable
fun ProjectListScreenTopBar(
    onCreateClicked: () -> Unit,
    onRequestClicked: () -> Unit
) {
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
        RequestButton(
            onRequestClicked = {
                onRequestClicked()
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        CreateButton(
            onCreateClicked = {
                onCreateClicked()
            }
        )
    }
}

@Composable
fun RequestButton(
    onRequestClicked: () -> Unit
) {
    Box(
        modifier = Modifier.clickable {
            onRequestClicked()
        }
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
fun CreateButton(
    onCreateClicked: () -> Unit
) {
    Box(
        modifier = Modifier.clickable {
            onCreateClicked()
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestProjectBottomSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onContinue: (String, String) -> Unit,
    onViewProject: (String, String) -> Unit,
    toastMessage: String?,
    toastType: ToastType,
    onClearToast: () -> Unit
) {

    if (!showSheet) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var part1 by remember { mutableStateOf("") }
    var part2 by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Text(
                text = "Request Project Access",
                style = textStyle(
                    size = 18.sp,
                    weight = FontWeight.Bold
                ),
                color = AppColors.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please enter the code to join the project",
                style = textStyle(
                    size = 13.sp,
                    weight = FontWeight.Normal
                ),
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painterResource(Res.drawable.ic_key),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = part1,
                    onValueChange = { if (it.length <= 4) part1 = it },
                    textStyle = textStyle(size = 16.sp, weight = FontWeight.Bold).copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = AppColors.Black),
                    singleLine = true,
                    modifier = Modifier
                        .width(80.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF4F4F4))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                        .padding(top = 13.dp)
                )
                
                Text(
                    text = " - ",
                    color = AppColors.SkyBlue,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = textStyle(size = 18.sp, weight = FontWeight.Bold)
                )
                
                androidx.compose.foundation.text.BasicTextField(
                    value = part2,
                    onValueChange = { if (it.length <= 5) part2 = it },
                    textStyle = textStyle(size = 16.sp, weight = FontWeight.Bold).copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = AppColors.Black),
                    singleLine = true,
                    modifier = Modifier
                        .width(90.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF4F4F4))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                        .padding(top = 13.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(30.dp))
            
            Text(
                text = "View Project",
                style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                color = AppColors.Primary,
                modifier = Modifier.clickable { onViewProject(part1, part2) }
            )
            
            Spacer(modifier = Modifier.height(30.dp))
            
            org.example.project.utilites.AppPrimaryButton(
                title = "Continue",
                onClick = { onContinue(part1, part2) },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            )
            }
            
            ToastHost(
                visible = toastMessage != null,
                message = toastMessage ?: "",
                onDismiss = onClearToast,
                type = toastType
            )
        }
    }
}

@Composable
fun ViewProjectDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    project: Project?
) {
    if (!showDialog || project == null) return
    
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss
    ) {
        androidx.compose.material3.Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
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
                        imageUrl = project.groupImage
                    )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = project.groupName ?: "",
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
                                    text = project.groupCode ?: "",
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

                project.description?.let { desc ->
                    Text(
                        text = desc,
                        style = textStyle(size = 13.sp, weight = FontWeight.Normal),
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                }
                
                org.example.project.utilites.AppPrimaryButton(
                    title = "Close",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )
            }
        }
    }
}