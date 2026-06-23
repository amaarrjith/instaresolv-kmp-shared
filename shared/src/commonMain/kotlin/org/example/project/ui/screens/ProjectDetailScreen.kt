package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import org.example.project.utilites.AppPrimaryButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_add_plus
import instaresolv.shared.generated.resources.ic_edit
import instaresolv.shared.generated.resources.ic_email
import instaresolv.shared.generated.resources.ic_invite_email
import instaresolv.shared.generated.resources.ic_right_icon
import instaresolv.shared.generated.resources.ic_search
import org.example.project.colors.AppColors
import org.example.project.data.model.ProjectDetail
import org.example.project.data.model.ProjectMember
import org.example.project.data.model.UserRole
import org.example.project.data.model.UserType
import org.example.project.project.ProjectDetailUiState
import org.example.project.project.ProjectDetailViewModel
import org.example.project.project.InviteStatus
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppSuccessDialog
import org.example.project.ui.components.AppExitDialog
import org.example.project.utilites.AppTextField
import androidx.compose.ui.text.style.TextAlign
import instaresolv.shared.generated.resources.ic_trash
import org.example.project.ui.components.AppLoader
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppSearchBar
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun ProjectDetailScreen(
    groupId: Int,
    groupCode: String,
    onBackClick: () -> Unit,
    onEditClick: (ProjectDetail) -> Unit
) {
    val viewModel: ProjectDetailViewModel = koinInject()
    val uiState = viewModel.uiState.collectAsState()
    val inviteStatus = viewModel.inviteStatus.collectAsState()
    val isAppAdmin = UserType.fromInt(viewModel.loggedInUser?.userType ?: -1) == UserType.APP_ADMIN
    val loggedInUserId = viewModel.loggedInUser?.userId ?: -1
    val successMessage = remember { mutableStateOf<String?>(null) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val isExitProjectClicked = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.getProjectDetails(
            groupId = groupId,
            groupCode = groupCode
        )
    }
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ProjectDetailScreenTopBar(
                uiState = uiState,
                onEditClick = { project ->
                    onEditClick(project)
                },
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .imePadding()
        ) {
            when(uiState.value) {
                is ProjectDetailUiState.Loading -> {
                    AppLoader()
                }
                is ProjectDetailUiState.Success -> {
                    ProjectDetailScreenContent(
                        isAppAdmin = isAppAdmin,
                        loggedInUserId = loggedInUserId,
                        project = (uiState.value as ProjectDetailUiState.Success).project,
                        onInviteClick = { emails ->
                            viewModel.inviteMembers(emails)
                        },
                        onDeleteClick = { password, resultCallback ->
                            viewModel.deleteProject(
                                password = password,
                                onSuccess = { msg ->
                                    resultCallback(null)
                                    successMessage.value = msg
                                },
                                onError = { errorMsg ->
                                    resultCallback(errorMsg)
                                }
                            )
                        },
                        onExitProjectClick = {
                            viewModel.exitProject(
                                onSuccess = { msg ->
                                    successMessage.value = msg
                                },
                                onError = { errorMsg ->
                                    errorMessage.value = errorMsg
                                }
                            )
                        },
                        onChangeRoleSubmit = { userId, newRole ->
                            viewModel.changeMemberRole(
                                userId = userId,
                                newRole = newRole,
                                onSuccess = { msg -> successMessage.value = msg },
                                onError = { msg -> errorMessage.value = msg }
                            )
                        },
                        onRemoveMemberSubmit = { userId ->
                            viewModel.removeMember(
                                userId = userId,
                                onSuccess = { msg -> successMessage.value = msg },
                                onError = { msg -> errorMessage.value = msg }
                            )
                        }
                    )
                }
                is ProjectDetailUiState.Error -> {
                    ErrorRetryView(
                        errorMessage = (uiState.value as ProjectDetailUiState.Error).errorMessage,
                        onRetryClick = {
                            viewModel.getProjectDetails(
                                groupId = groupId,
                                groupCode = groupCode
                            )
                        }
                    )
                }
            }

            if (inviteStatus.value is InviteStatus.Error) {
                ToastHost(
                    visible = true,
                    message = (inviteStatus.value as InviteStatus.Error).message,
                    onDismiss = {
                        viewModel.clearInviteStatus()
                    },
                    type = ToastType.Error
                )
            }
            
            if (inviteStatus.value is InviteStatus.Success) {
                AppSuccessDialog(
                    visible = true,
                    title = "Success",
                    description = "Member(s) invited successfully!",
                    onDismiss = {
                        viewModel.clearInviteStatus()
                    }
                )
            }

            if (successMessage.value != null) {
                AppSuccessDialog(
                    visible = true,
                    title = "Success",
                    description = successMessage.value ?: "",
                    onDismiss = {
                        successMessage.value = null
                        onBackClick()
                    }
                )
            }

            if (errorMessage.value != null) {
                ToastHost(
                    visible = true,
                    message = errorMessage.value ?: "",
                    onDismiss = {
                        errorMessage.value = null
                    },
                    type = ToastType.Error
                )
            }
        }
    }
}

@Composable
fun ProjectDetailScreenContent(
    isAppAdmin: Boolean,
    loggedInUserId: Int,
    project: ProjectDetail,
    onInviteClick: (List<String>) -> Unit,
    onDeleteClick: (String, (String?) -> Unit) -> Unit,
    onExitProjectClick: () -> Unit,
    onChangeRoleSubmit: (Int, Int) -> Unit,
    onRemoveMemberSubmit: (Int) -> Unit
) {
    val isSearchBarVisible = remember {
        mutableStateOf(false)
    }
    val searchQuery = remember {
        mutableStateOf("")
    }
    val showInviteSheet = remember { mutableStateOf(false) }
    val showDeleteSheet = remember { mutableStateOf(false) }
    val showExitDialog = remember { mutableStateOf(false) }
    val showChangeRoleSheet = remember { mutableStateOf<ProjectMember?>(null) }
    val showChangeDesignationSheet = remember { mutableStateOf<ProjectMember?>(null) }
    val showLeaveProjectDialog = remember { mutableStateOf<ProjectMember?>(null) }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        WebImageView(
            imageUrl = project.groupImage,
            modifier = Modifier.fillMaxWidth()
                .height(220.dp)
        )
        Spacer(modifier = Modifier.height(22.dp))
        Column(
            modifier = Modifier.padding(horizontal = 22.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray)
            ) {
                Text(
                    text = project.groupCode,
                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 6.dp),
                    style = textStyle(size = 10.sp, weight = FontWeight.SemiBold),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = project.groupName,
                style = textStyle(
                    size = 16.sp,
                    weight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = project.description,
                style = textStyle(
                    size = 14.sp,
                    weight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Text(
                    text = "Project Members",
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        isSearchBarVisible.value = !isSearchBarVisible.value
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                AddMemberIcon(
                    onClick = { showInviteSheet.value = true }
                )
            }
            if (isSearchBarVisible.value) {
                AppSearchBar(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    placeholder = "Search members...",
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            
            val filteredMembers = project.members.filter {
                it.name.contains(searchQuery.value, ignoreCase = true)
            }
            ProjectMembersScreen(
                members = filteredMembers,
                isAppAdmin = isAppAdmin,
                isProjectAdmin = project.isAdmin,
                loggedInUserId = loggedInUserId,
                onChangeRoleClick = { showChangeRoleSheet.value = it },
                onChangeDesignationClick = { showChangeDesignationSheet.value = it },
                onLeaveProjectClick = { showLeaveProjectDialog.value = it }
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 20.dp)
            )
            SettingsContentView(
                isAppAdmin = isAppAdmin,
                isProjectAdmin = project.isAdmin,
                trainingFileUrl = project.trainingFileUrl,
                onDeleteProjectClick = { showDeleteSheet.value = true },
                onExitProjectClick = { showExitDialog.value = true }
            )
            Spacer(modifier = Modifier.height(50.dp))
        }
    }

    if (showInviteSheet.value) {
        InviteMemberBottomSheet(
            onDismiss = { showInviteSheet.value = false },
            onInvite = { emails ->
                onInviteClick(emails)
                showInviteSheet.value = false
            }
        )
    }

    if (showDeleteSheet.value) {
        DeleteProjectBottomSheet(
            onDismiss = { showDeleteSheet.value = false },
            onDelete = { password, resultCallback ->
                onDeleteClick(password, resultCallback)
            }
        )
    }

    if (showExitDialog.value) {
        AppExitDialog(
            visible = true,
            onConfirm = {
                onExitProjectClick()
                showExitDialog.value = false
            },
            onDismiss = { showExitDialog.value = false }
        )
    }

    if (showChangeRoleSheet.value != null) {
        ChangeRoleBottomSheet(
            member = showChangeRoleSheet.value!!,
            onDismiss = { showChangeRoleSheet.value = null },
            onSubmit = { newRole ->
                onChangeRoleSubmit(showChangeRoleSheet.value!!.userId, newRole)
                showChangeRoleSheet.value = null
            }
        )
    }

    if (showChangeDesignationSheet.value != null) {
        ChangeDesignationBottomSheet(
            member = showChangeDesignationSheet.value!!,
            onDismiss = { showChangeDesignationSheet.value = null }
        )
    }

    if (showLeaveProjectDialog.value != null) {
        AppExitDialog(
            visible = true,
            onConfirm = {
                val member = showLeaveProjectDialog.value!!
                if (member.userId == loggedInUserId) {
                    onExitProjectClick()
                } else {
                    onRemoveMemberSubmit(member.userId)
                }
                showLeaveProjectDialog.value = null
            },
            onDismiss = { showLeaveProjectDialog.value = null }
        )
    }
}

@Composable
fun SettingsContentView(
    isAppAdmin: Boolean,
    isProjectAdmin: Boolean,
    trainingFileUrl: String,
    onDeleteProjectClick: () -> Unit = {},
    onExitProjectClick: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Settings",
            style = textStyle(
                size = 14.sp,
                weight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        when {
            isProjectAdmin -> {
                SettingsItemRow(
                    title = "Transfer Admin Rights",
                    subtitle = "Hand over admin rights to a project member",
                    onClick = {}
                )

                if (trainingFileUrl.isNotBlank()) {
                    SettingsItemRow(
                        title = "Export Training Details",
                        subtitle = "Download all members' training details",
                        onClick = {}
                    )
                }

                SettingsItemRow(
                    title = "Exit Project",
                    subtitle = "Exit from this project",
                    isRed = true,
                    onClick = onExitProjectClick
                )
            }

            isAppAdmin -> {
                if (trainingFileUrl.isNotBlank()) {
                    SettingsItemRow(
                        title = "Export Training Details",
                        subtitle = "Download all members' training details",
                        onClick = {}
                    )
                }

                SettingsItemRow(
                    title = "Delete Project",
                    subtitle = "Permanently delete this project",
                    isRed = true,
                    onClick = onDeleteProjectClick
                )
            }

            else -> {
                SettingsItemRow(
                    title = "Exit Project",
                    subtitle = "Exit from this project",
                    isRed = true,
                    onClick = onExitProjectClick
                )
            }
        }
    }
}

@Composable
fun SettingsItemRow(
    title: String,
    subtitle: String,
    isRed: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = textStyle(
                    size = 13.sp,
                    weight = FontWeight.Bold
                ),
                color = if (isRed) {AppColors.Primary} else {
                    AppColors.Black
                }
            )
            Text(
                text = subtitle,
                style = textStyle(
                    size = 11.sp,
                    weight = FontWeight.Normal
                ),
                color = AppColors.TextGray
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(Res.drawable.ic_right_icon),
            contentDescription = null
        )
    }
}

@Composable
fun ProjectMembersScreen(
    members: List<ProjectMember>,
    isAppAdmin: Boolean,
    isProjectAdmin: Boolean,
    loggedInUserId: Int,
    onChangeRoleClick: (ProjectMember) -> Unit,
    onChangeDesignationClick: (ProjectMember) -> Unit,
    onLeaveProjectClick: (ProjectMember) -> Unit
) {
    if (members.isEmpty()) {
        EmptyScreenView(
            message = "No Members Found"
        )
    } else {
        Column {
            repeat(members.size) { index ->
                val member = members[index]
                ProjectMembersItemRow(
                    member = member,
                    isAppAdmin = isAppAdmin,
                    isProjectAdmin = isProjectAdmin,
                    loggedInUserId = loggedInUserId,
                    onChangeRoleClick = { onChangeRoleClick(member) },
                    onChangeDesignationClick = { onChangeDesignationClick(member) },
                    onLeaveProjectClick = { onLeaveProjectClick(member) }
                )
            }
        }
    }
}

@Composable
fun AddMemberIcon(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_add_plus),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Member",
            style = textStyle(
                size = 13.sp,
                weight = FontWeight.SemiBold
            ),
            color = AppColors.Primary
        )
    }
}

@Composable
fun MemberActionButton(
    title: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = color,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = textStyle(
                size = 12.sp,
                weight = FontWeight.Medium
            ),
            color = color
        )
    }
}

@Composable
fun ProjectMembersItemRow(
    member: ProjectMember,
    isAppAdmin: Boolean,
    isProjectAdmin: Boolean,
    loggedInUserId: Int,
    onChangeRoleClick: () -> Unit,
    onChangeDesignationClick: () -> Unit,
    onLeaveProjectClick: () -> Unit
) {
    val isExpanded = remember { mutableStateOf(false) }
    val canExpand = isAppAdmin || isProjectAdmin

    Column(
        modifier = Modifier.fillMaxWidth()
            .clickable(enabled = canExpand) {
                isExpanded.value = !isExpanded.value
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WebImageView(
                imageUrl = member.image,
                modifier = Modifier.size(50.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = member.name,
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_email),
                        contentDescription = null
                    )
                    Text(
                        text = member.email,
                        style = textStyle(
                            size = 12.sp,
                            weight = FontWeight.Normal
                        ),
                        color = AppColors.TextGray
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            MemberStatusIcon(UserRole.fromInt(member.role) == UserRole.ADMIN)
        }

        if (isExpanded.value) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 64.dp, end = 16.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MemberActionButton(
                        title = "Change Role",
                        color = AppColors.SkyBlue,
                        onClick = onChangeRoleClick,
                        modifier = Modifier.weight(1f)
                    )
                    val leaveText = if (member.userId == loggedInUserId) "Exit Project" else "Remove User"
                    MemberActionButton(
                        title = leaveText,
                        color = AppColors.Primary,
                        onClick = onLeaveProjectClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                MemberActionButton(
                    title = "Change Designation",
                    color = AppColors.SkyBlue,
                    onClick = onChangeDesignationClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MemberStatusIcon(isAdmin: Boolean) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (isAdmin) AppColors.Primary else AppColors.TextGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (isAdmin) "ADMIN" else "PARTICIPANT",
            style = textStyle(
                size = 10.sp,
                weight = FontWeight.SemiBold
            ),
            color = if (isAdmin) AppColors.Primary else AppColors.TextGray
        )
    }
}

@Composable
fun ProjectDetailScreenTopBar(
    uiState: State<ProjectDetailUiState>,
    onEditClick: (ProjectDetail) -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .statusBarsPadding()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationBackIcon(
            onClick = {
                onBackClick()
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Project".uppercase(),
            style = textStyle(
                size = 14.sp,
                weight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        when(uiState.value) {
            is ProjectDetailUiState.Loading -> {

            }
            is ProjectDetailUiState.Error -> {

            } else -> {
            Row(
                modifier = Modifier
                    .padding(end = 26.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_edit),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit".uppercase(),
                    modifier = Modifier.clickable {
                        onEditClick((uiState.value as ProjectDetailUiState.Success).project)
                    },
                    style = textStyle(
                        size = 13.sp,
                        weight = FontWeight.SemiBold
                    ),
                    color = AppColors.SkyBlue
                )
            }
        }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InviteMemberBottomSheet(
    onDismiss: () -> Unit,
    onInvite: (List<String>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val emailList = remember { mutableStateListOf<String>() }
    val currentEmail = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Box(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 22.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Invite Member",
                    style = textStyle(size = 20.sp, weight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Enter an email address to invite a user",
                    style = textStyle(size = 14.sp, weight = FontWeight.Normal),
                    color = AppColors.TextGray
                )
                Spacer(modifier = Modifier.height(30.dp))

                // NOTE: Using ic_email as a placeholder until you add ic_invite_icon.
                // Replace Res.drawable.ic_email with Res.drawable.ic_invite_icon after adding it.
                Image(
                    painter = painterResource(Res.drawable.ic_invite_email),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(30.dp))

                if (emailList.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        emailList.forEach { email ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF4F4F4))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = email,
                                        style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                        color = AppColors.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "×",
                                        style = textStyle(size = 16.sp, weight = FontWeight.Bold),
                                        color = AppColors.TextGray,
                                        modifier = Modifier.clickable {
                                            emailList.remove(email)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = currentEmail.value,
                    onValueChange = { currentEmail.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Medium
                    ),
                    placeholder = {
                        Text(
                            text = "Email Address",
                            style = textStyle(
                                size = 14.sp,
                                weight = FontWeight.Medium
                            ),
                            color = Color(0xFF9E9E9E)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val email = currentEmail.value.trim()

                                when {
                                    email.isBlank() -> {
                                        errorMessage.value = "Please enter an email address"
                                    }

                                    !email.contains("@") -> {
                                        errorMessage.value = "Please enter a valid email address"
                                    }

                                    emailList.contains(email) -> {
                                        errorMessage.value = "This email has already been added"
                                    }

                                    else -> {
                                        emailList.add(email)
                                        currentEmail.value = ""
                                        errorMessage.value = null
                                    }
                                }
                            }
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_add_plus),
                                contentDescription = "Add email"
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF4F4F4),
                        unfocusedContainerColor = Color(0xFFF4F4F4),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = AppColors.Primary
                    )
                )
                Spacer(modifier = Modifier.height(30.dp))
                AppPrimaryButton(
                    title = "Invite",
                    onClick = {
                        val emailsToInvite = emailList.toMutableList()

                        val email = currentEmail.value.trim()
                        if (email.isNotBlank() && !emailsToInvite.contains(email)) {
                            emailsToInvite.add(email)
                        }

                        if (emailsToInvite.isEmpty()) {
                            errorMessage.value = "Please add at least one email address"
                            return@AppPrimaryButton
                        }

                        errorMessage.value = null
                        onInvite(emailsToInvite)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            ToastHost(
                visible = errorMessage.value != null,
                message = errorMessage.value ?: "",
                onDismiss = {
                    errorMessage.value = null
                },
                type = ToastType.Error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteProjectBottomSheet(
    onDismiss: () -> Unit,
    onDelete: (String, (String?) -> Unit) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Box(modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 22.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()

                    .padding(bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // NOTE: Replace ic_email with your delete icon (e.g. ic_delete) when added
                Image(
                    painter = painterResource(Res.drawable.ic_trash),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = "Delete Project ?",
                    style = textStyle(size = 20.sp, weight = FontWeight.Bold),
                    color = Color(0xFFD32F2F)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Are you sure you want to delete the\nproject permanently ?",
                    style = textStyle(size = 14.sp, weight = FontWeight.Normal),
                    color = AppColors.TextGray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(30.dp))
                
                AppTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    title = "Password",
                    placeholder = "********",
                    isSecure = true
                )
                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        if (password.value.isNotBlank()) {
                            onDelete(password.value) { errorMsg ->
                                if (errorMsg != null) {
                                    errorMessage.value = errorMsg
                                } else {
                                    onDismiss()
                                }
                            }
                        } else {
                            errorMessage.value = "Please enter your password"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text(
                        text = "Delete Permanently",
                        style = textStyle(size = 16.sp, weight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Not Now",
                    style = textStyle(size = 16.sp, weight = FontWeight.SemiBold),
                    color = AppColors.TextGray,
                    modifier = Modifier.clickable { onDismiss() }.padding(8.dp)
                )
            }
            
            ToastHost(
                visible = errorMessage.value != null,
                message = errorMessage.value ?: "",
                onDismiss = { errorMessage.value = null },
                type = ToastType.Error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeRoleBottomSheet(
    member: ProjectMember,
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val selectedRole = remember { mutableStateOf(if (UserRole.fromInt(member.role) == UserRole.ADMIN) UserRole.ADMIN else UserRole.PARTICIPANT) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp, start = 22.dp, end = 22.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Change Role for ${member.name}",
                style = textStyle(size = 18.sp, weight = FontWeight.Bold)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.RadioButton(
                        selected = selectedRole.value == UserRole.ADMIN,
                        onClick = { selectedRole.value = UserRole.ADMIN },
                        colors = androidx.compose.material3.RadioButtonDefaults.colors(
                            selectedColor = AppColors.Primary
                        )
                    )
                    Text("Admin", style = textStyle(size = 14.sp, weight = FontWeight.Medium))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.RadioButton(
                        selected = selectedRole.value == UserRole.PARTICIPANT,
                        onClick = { selectedRole.value = UserRole.PARTICIPANT },
                        colors = androidx.compose.material3.RadioButtonDefaults.colors(
                            selectedColor = AppColors.Primary
                        )
                    )
                    Text("Participant", style = textStyle(size = 14.sp, weight = FontWeight.Medium))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppBorderButton(
                    title = "Cancel",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
                AppPrimaryButton(
                    title = "Submit",
                    onClick = {
                        onSubmit(selectedRole.value.value)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeDesignationBottomSheet(
    member: ProjectMember,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp, start = 22.dp, end = 22.dp)) {
            Text("Change Designation for ${member.name}", style = textStyle(size = 18.sp, weight = FontWeight.Bold))
        }
    }
}
