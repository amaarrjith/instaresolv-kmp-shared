package org.example.project.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.colors.AppColors
import org.example.project.data.model.LessonLearnedImageRequest
import org.example.project.data.model.ObservationImage
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppProjectDropdown
import org.example.project.ui.components.AppProjectDropdownViewModel
import org.example.project.ui.components.AppImageCreateBox
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppTextInput
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.koin.compose.koinInject
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_add
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.ColorFilter
import org.jetbrains.compose.resources.painterResource

@Composable
fun CreateLessonsLearnedScreen(
    onBackClicked: () -> Unit
) {
    val viewModel: CreateLessonLearnedViewModel = koinInject()
    val projectDropdownViewModel: AppProjectDropdownViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var facilitiesId by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reportedBy by remember { mutableStateOf("") }
    
    val images = remember { mutableStateListOf<ObservationImage>() }

    var showErrorToast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        when (uiState) {
            is CreateLessonLearnedUiState.Success -> {
                viewModel.resetState()
                onBackClicked()
            }
            is CreateLessonLearnedUiState.Error -> {
                showErrorToast = (uiState as CreateLessonLearnedUiState.Error).message
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color.White).statusBarsPadding().navigationBarsPadding(),
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(onBackClick = onBackClicked)
                Text(
                    text = "Create Lesson Learned",
                    modifier = Modifier.weight(1f),
                    style = textStyle(18.sp, FontWeight.SemiBold),
                    color = AppColors.Black
                )
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                AppPrimaryButton(
                    text = "SUBMIT",
                    onClick = {
                        if (title.isBlank()) {
                            showErrorToast = "Title is required"
                            return@AppPrimaryButton
                        }
                        if (reportedBy.isBlank()) {
                            showErrorToast = "Reported By is required"
                            return@AppPrimaryButton
                        }
                        
                        val imageRequests = images.filter { it.imageUrl.isNotBlank() }.map {
                            LessonLearnedImageRequest(
                                image = it.imageUrl,
                                description = it.description,
                                isAiGeneratedDescription = false // Defaulting to false since no AI generation in this form
                            )
                        }
                        
                        viewModel.createLessonLearned(
                            title = title,
                            description = description.takeIf { it.isNotBlank() },
                            reportedBy = reportedBy,
                            images = imageRequests.takeIf { it.isNotEmpty() },
                            facilitiesId = facilitiesId
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState is CreateLessonLearnedUiState.Loading
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            
            AppProjectDropdown(
                viewModel = projectDropdownViewModel,
                onProjectSelected = { project ->
                    facilitiesId = project?.groupId
                },
                isRequired = false
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AppTextInput(
                value = title,
                onValueChange = { title = it },
                title = "Title",
                hint = "Enter title",
                isRequired = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AppTextInput(
                value = description,
                onValueChange = { description = it },
                title = "Description",
                hint = "Enter description",
                maxLines = 5,
                isRequired = false
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AppTextInput(
                value = reportedBy,
                onValueChange = { reportedBy = it },
                title = "Reported By",
                hint = "Enter reported by",
                isRequired = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Images (Optional)",
                style = textStyle(14.sp, FontWeight.Medium),
                color = AppColors.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            images.forEachIndexed { index, image ->
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Text(
                        text = "Image ${index + 1}",
                        style = textStyle(12.sp, FontWeight.Normal),
                        color = AppColors.TextGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppImageCreateBox(
                        imageUrl = image.imageUrl,
                        description = image.description,
                        onDescriptionChange = { newDesc ->
                            images[index] = image.copy(description = newDesc)
                        },
                        onImageUploaded = { newUrl ->
                            images[index] = image.copy(imageUrl = newUrl)
                        },
                        onRemoveImageClick = {
                            images.removeAt(index)
                        }
                    )
                }
            }
            
            if (images.size < 5) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { images.add(ObservationImage()) }.padding(vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_add),
                        contentDescription = "Add Image",
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(AppColors.Primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Image",
                        style = textStyle(14.sp, FontWeight.Medium),
                        color = AppColors.Primary
                    )
                }
            }
        }
    }
    
    showErrorToast?.let {
        ToastHost(
            message = it,
            isSuccess = false,
            onDismiss = { showErrorToast = null }
        )
    }
}
