package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import org.example.project.colors.AppColors
import org.example.project.data.model.Project
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.example.project.ui.screens.EmptyScreenView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppProjectDropdown(
    title: String = "Project",
    placeholder: String = "Select Project",
    selectedProject: Project?,
    onProjectSelected: (Project?) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AppProjectDropdownViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = textStyle(
                size = 12.sp,
                weight = FontWeight.SemiBold
            ),
            color = AppColors.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp)
                    .menuAnchor(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedProject != null) {
                    WebImageView(
                        imageUrl = selectedProject.groupImage ?: "",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFEFEF))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (selectedProject?.groupName.isNullOrEmpty()) {
                        Text(
                            text = placeholder,
                            style = textStyle(
                                size = 14.sp,
                                weight = FontWeight.Medium
                            ),
                            color = Color(0xFF9E9E9E)
                        )
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = selectedProject?.groupName ?: "",
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        readOnly = true,
                        textStyle = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Medium
                        ).copy(color = AppColors.Black),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(AppColors.Primary)
                    )
                }

                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                if (uiState.isLoading) {
                    DropdownMenuItem(
                        text = { Text("Loading...") },
                        onClick = { }
                    )
                } else if (uiState.projects.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No Projects Found") },
                        onClick = { }
                    )
                } else {
                    uiState.projects.forEach { project ->
                        DropdownMenuItem(
                            text = { 
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    WebImageView(
                                        imageUrl = project.groupImage ?: "",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEFEFEF))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = project.groupName ?: "Unknown",
                                            style = textStyle(size = 15.sp, weight = FontWeight.Medium),
                                            color = AppColors.Black
                                        )
                                        Text(
                                            text = project.groupCode ?: "",
                                            style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                                            color = Color(0xFF8F9098)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onProjectSelected(project)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
