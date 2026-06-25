package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.*
import org.example.project.data.model.FilterContentData
import org.example.project.data.model.AppFilterState
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.ui.screens.EmptyScreenView
import org.example.project.ui.components.WebImageView
import org.example.project.ui.components.AppDatePicker
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFilterBottomSheet(
    filterData: FilterContentData?,
    appliedFilterState: AppFilterState,
    onApply: (AppFilterState) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val tabs = listOf("Status", "Project", "Observer", "Responsible", "Date")
    var selectedTab by remember { mutableStateOf(tabs[0]) }
    
    var tempStatuses by remember { mutableStateOf(appliedFilterState.selectedStatuses) }
    var tempProjects by remember { mutableStateOf(appliedFilterState.selectedProjects) }
    var tempNoProjectSelected by remember { mutableStateOf(appliedFilterState.noProjectSelected) }
    var tempObservers by remember { mutableStateOf(appliedFilterState.selectedObservers) }
    var tempResponsiblePersons by remember { mutableStateOf(appliedFilterState.selectedResponsiblePersons) }
    var dateOpenMillis by remember { mutableStateOf(appliedFilterState.dateOpenMillis) }
    var dateCloseMillis by remember { mutableStateOf(appliedFilterState.dateCloseMillis) }

    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { androidx.compose.material3.BottomSheetDefaults.DragHandle() },
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.75f)) {
            HorizontalDivider(color = Color(0xFFE5E5E5))
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                // Left Column
                Column(
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxHeight()
                        .background(Color(0xFFF3F3F3))
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        val hasBadge = when(tab) {
                            "Status" -> tempStatuses.isNotEmpty()
                            "Project" -> tempProjects.isNotEmpty() || tempNoProjectSelected
                            "Observer" -> tempObservers.isNotEmpty()
                            "Responsible" -> tempResponsiblePersons.isNotEmpty()
                            "Date" -> dateOpenMillis != null || dateCloseMillis != null
                            else -> false
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .clickable { selectedTab = tab }
                                .padding(vertical = 16.dp, horizontal = 16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tab,
                                    style = textStyle(
                                        size = 14.sp,
                                        weight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                                    ),
                                    color = AppColors.Black
                                )
                                if (hasBadge) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier.size(6.dp).clip(CircleShape).background(AppColors.Primary)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Right Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTab,
                            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                            color = AppColors.TextGray
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Clear All",
                            style = textStyle(size = 13.sp, weight = FontWeight.Bold),
                            color = AppColors.Primary,
                            modifier = Modifier.clickable { 
                                tempStatuses = emptyList()
                                tempProjects = emptyList()
                                tempNoProjectSelected = false
                                tempObservers = emptyList()
                                tempResponsiblePersons = emptyList()
                                dateOpenMillis = null
                                dateCloseMillis = null
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        if (selectedTab == "Status") {
                            val statusOptions = listOf("Open Observations", "Closed Observations")
                            item {
                                statusOptions.forEach { option ->
                                     Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempStatuses = if (tempStatuses.contains(option)) {
                                                    tempStatuses - option
                                                } else {
                                                    tempStatuses + option
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(if (tempStatuses.contains(option)) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off),
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = option,
                                            style = textStyle(size = 15.sp, weight = FontWeight.Medium),
                                            color = AppColors.Black
                                        )
                                    }
                                }
                            }
                        } else if (selectedTab == "Project") {
                            val projects = filterData?.projects ?: emptyList()
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            tempNoProjectSelected = !tempNoProjectSelected
                                            if (tempNoProjectSelected) tempProjects = emptyList()
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "No Project", 
                                            style = textStyle(size = 15.sp, weight = FontWeight.Medium), 
                                            color = AppColors.Black
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Show all observations that are not specified to any project", 
                                            style = textStyle(size = 12.sp, weight = FontWeight.Normal), 
                                            color = Color(0xFF8F9098)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Image(
                                        painter = painterResource(if (tempNoProjectSelected) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off), 
                                        contentDescription = null, 
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            if (projects.isEmpty()) {
                                item {
                                    EmptyScreenView(
                                        "No Projects Found"
                                    )
                                }
                            } else {
                                items(projects.size) { index ->
                                    val project = projects[index]
                                    val isChecked = tempProjects.any { it.groupId == project.groupId }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempProjects = if (isChecked) {
                                                    tempProjects.filterNot { it.groupId == project.groupId }
                                                } else {
                                                    tempNoProjectSelected = false
                                                    tempProjects + project
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        org.example.project.ui.components.WebImageView(
                                            imageUrl = projects[index].groupImage,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFF2F2F2))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        androidx.compose.foundation.layout.Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = projects[index].groupName,
                                                style = textStyle(size = 15.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF94979D), RoundedCornerShape(12.dp))
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = projects[index].groupCode,
                                                    style = textStyle(size = 10.sp, weight = FontWeight.Medium),
                                                    color = Color.White
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Image(
                                            painter = painterResource(if (isChecked) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off),
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        } else if (selectedTab == "Responsible") {
                            val persons = filterData?.responsiblePersons ?: emptyList()
                            if (persons.isEmpty()) {
                                item {
                                    EmptyScreenView(
                                        "No Responsible Person Found"
                                    )
                                }
                            } else {
                                items(persons.size) { index ->
                                    val person = persons[index]
                                    val isChecked = tempResponsiblePersons.any { it.userId == person.userId }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempResponsiblePersons = if (isChecked) {
                                                    tempResponsiblePersons.filterNot { it.userId == person.userId }
                                                } else {
                                                    tempResponsiblePersons + person
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        org.example.project.ui.components.WebImageView(
                                            imageUrl = persons[index].image,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(androidx.compose.foundation.shape.CircleShape)
                                                .background(Color(0xFFF2F2F2))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        androidx.compose.foundation.layout.Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = persons[index].name,
                                                style = textStyle(size = 15.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Image(
                                                    painter = painterResource(Res.drawable.ic_email),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(12.dp),
                                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF8F9098))
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = persons[index].email,
                                                    style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                                                    color = Color(0xFF8F9098)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Image(
                                            painter = painterResource(if (isChecked) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off),
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        } else if (selectedTab == "Observer") {
                            val observers = filterData?.responsiblePersons ?: emptyList()
                            if (observers.isEmpty()) {
                                item {
                                    EmptyScreenView(
                                        "No Observer Found"
                                    )
                                }
                            } else {
                                items(observers.size) { index ->
                                    val observer = observers[index]
                                    val isChecked = tempObservers.any { it.userId == observer.userId }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempObservers = if (isChecked) {
                                                    tempObservers.filterNot { it.userId == observer.userId }
                                                } else {
                                                    tempObservers + observer
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        org.example.project.ui.components.WebImageView(
                                            imageUrl = observers[index].image,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(androidx.compose.foundation.shape.CircleShape)
                                                .background(Color(0xFFF2F2F2))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        androidx.compose.foundation.layout.Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = observers[index].name,
                                                style = textStyle(size = 15.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Image(
                                                    painter = painterResource(Res.drawable.ic_email),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(12.dp),
                                                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF8F9098))
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = observers[index].email,
                                                    style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                                                    color = Color(0xFF8F9098)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Image(
                                            painter = painterResource(if (isChecked) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off),
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        } else if (selectedTab == "Date") {
                            item {
                                androidx.compose.foundation.layout.Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    org.example.project.ui.components.AppDatePicker(
                                        text = "Date Open",
                                        onDateSelected = { dateOpenMillis = it },
                                        selectedDateMillis = dateOpenMillis
                                    )
                                    org.example.project.ui.components.AppDatePicker(
                                        text = "Date Close",
                                        onDateSelected = { dateCloseMillis = it },
                                        selectedDateMillis = dateCloseMillis
                                    )
                                }
                            }
                        } else {

                        }
                    }
                }
            }
            
            // Bottom Bar
            HorizontalDivider(color = Color(0xFFE5E5E5))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.material3.OutlinedButton(
                    onClick = { /* Handle Export */ },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Primary),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Export All",
                            color = AppColors.Primary,
                            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold)
                        )
                    }
                }
                
                androidx.compose.material3.Button(
                    onClick = {
                        onApply(
                            AppFilterState(
                                selectedStatuses = tempStatuses,
                                selectedProjects = tempProjects,
                                noProjectSelected = tempNoProjectSelected,
                                selectedResponsiblePersons = tempResponsiblePersons,
                                selectedObservers = tempObservers,
                                dateOpenMillis = dateOpenMillis,
                                dateCloseMillis = dateCloseMillis
                            )
                        )
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Text(
                        text = "Apply Filter",
                        color = Color.White,
                        style = textStyle(size = 14.sp, weight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}
