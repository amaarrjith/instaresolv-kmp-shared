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
import org.jetbrains.compose.resources.painterResource
import org.example.project.ui.IncidentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFilterBottomSheet(
    filterData: FilterContentData?,
    appliedFilterState: AppFilterState,
    isFromObservation: Boolean = false,
    isFromIncident: Boolean = false,
    isFromPermit: Boolean = false,
    moduleName: String = "Observations",
    onApply: (AppFilterState) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val tabs = remember(isFromObservation, isFromIncident, isFromPermit) {
        val baseTabs = mutableListOf("Project", "Reported By", "Date")
        if (isFromObservation) {
            baseTabs.add(0, "Status")
        }
        if (isFromIncident) {
            baseTabs.add("Incident Type")
        }
        baseTabs
    }
    var selectedTab by remember(tabs) { mutableStateOf(tabs[0]) }
    
    var tempStatuses by remember { mutableStateOf(appliedFilterState.selectedStatuses) }
    var tempProjects by remember { mutableStateOf(appliedFilterState.selectedProjects) }
    var tempNoProjectSelected by remember { mutableStateOf(appliedFilterState.noProjectSelected) }
    var tempObservers by remember { mutableStateOf(appliedFilterState.selectedObservers) }
    var tempResponsiblePersons by remember { mutableStateOf(appliedFilterState.selectedResponsiblePersons) }
    var tempReportedBy by remember { mutableStateOf(appliedFilterState.selectedReportedBy) }
    var tempIncidentTypes by remember { mutableStateOf(appliedFilterState.selectedIncidentTypes) }
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
                // Sidebar
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(Color(0xFFF2F2F2))
                        .width(135.dp)
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        val hasActiveFilter = when(tab) {
                            "Status" -> tempStatuses.isNotEmpty()
                            "Project" -> tempProjects.isNotEmpty() || tempNoProjectSelected
                            "Reported By" -> tempReportedBy.isNotEmpty()
                            "Date" -> dateOpenMillis != null || dateCloseMillis != null
                            "Incident Type" -> tempIncidentTypes.isNotEmpty()
                            "Observer" -> tempObservers.isNotEmpty()
                            "Responsible" -> tempResponsiblePersons.isNotEmpty()
                            else -> false
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTab = tab }
                                .background(if (isSelected) Color.White else Color.Transparent)
                                .padding(vertical = 14.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tab,
                                style = textStyle(size = 14.sp, weight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium),
                                color = if (isSelected) AppColors.Black else Color(0xFF94979D),
                                modifier = Modifier.weight(1f)
                            )
                            if (hasActiveFilter) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AppColors.Primary)
                                )
                            }
                        }
                        if (isSelected) {
                            HorizontalDivider(color = Color(0xFFE5E5E5))
                        }
                    }
                }
                
                // Content area
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
                                tempReportedBy = emptyList()
                                tempIncidentTypes = emptyList()
                                dateOpenMillis = null
                                dateCloseMillis = null
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        if (selectedTab == "Status") {
                            val statusOptions = listOf("Open $moduleName", "Closed $moduleName")
                            item {
                                statusOptions.forEach { option ->
                                     Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempStatuses = if (tempStatuses.contains(option)) {
                                                    tempStatuses.filterNot { it == option }
                                                } else {
                                                    tempStatuses + option
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val isChecked = tempStatuses.contains(option)
                                        Text(
                                            text = option,
                                            style = textStyle(size = 15.sp, weight = FontWeight.Medium),
                                            color = AppColors.Black
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Image(
                                            painter = painterResource(if (isChecked) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off),
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp)
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
                                            if (tempNoProjectSelected) {
                                                tempNoProjectSelected = false
                                            } else {
                                                tempNoProjectSelected = true
                                                tempProjects = emptyList()
                                            }
                                        }
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Not Specified", 
                                            style = textStyle(size = 15.sp, weight = FontWeight.Medium), 
                                            color = AppColors.Black
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Show all items that are not specified to any project", 
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
                                item { EmptyScreenView("No Projects Found") }
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
                                        WebImageView(
                                            imageUrl = projects[index].groupImage,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFF2F2F2))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
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
                                item { EmptyScreenView("No Responsible Person Found") }
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
                                        WebImageView(
                                            imageUrl = person.image,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF2F2F2))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = person.name,
                                                style = textStyle(size = 15.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Image(
                                                    painter = painterResource(Res.drawable.ic_email),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(12.dp),
                                                    colorFilter = ColorFilter.tint(Color(0xFF8F9098))
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = person.email,
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
                            val observers = filterData?.observers ?: emptyList() // Fixed to use observers instead of responsiblePersons
                            if (observers.isEmpty()) {
                                item { EmptyScreenView("No Observer Found") }
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
                                        WebImageView(
                                            imageUrl = observer.image,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF2F2F2))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = observer.name,
                                                style = textStyle(size = 15.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Image(
                                                    painter = painterResource(Res.drawable.ic_email),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(12.dp),
                                                    colorFilter = ColorFilter.tint(Color(0xFF8F9098))
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = observer.email,
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
                        } else if (selectedTab == "Reported By") {
                            // Combine responsiblePersons and observers as per the prompt
                            val allUsers = ((filterData?.responsiblePersons ?: emptyList()) + (filterData?.observers ?: emptyList())).distinctBy { it.userId }
                            if (allUsers.isEmpty()) {
                                item { EmptyScreenView("No Users Found") }
                            } else {
                                items(allUsers.size) { index ->
                                    val user = allUsers[index]
                                    val isChecked = tempReportedBy.any { it.userId == user.userId }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempReportedBy = if (isChecked) {
                                                    tempReportedBy.filterNot { it.userId == user.userId }
                                                } else {
                                                    tempReportedBy + user
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        WebImageView(
                                            imageUrl = user.image,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF2F2F2))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = user.name,
                                                style = textStyle(size = 15.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Image(
                                                    painter = painterResource(Res.drawable.ic_email),
                                                    contentDescription = null,
                                                    modifier = Modifier.size(12.dp),
                                                    colorFilter = ColorFilter.tint(Color(0xFF8F9098))
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = user.email,
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
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    AppDatePicker(
                                        text = "Date Open",
                                        onDateSelected = { dateOpenMillis = it },
                                        selectedDateMillis = dateOpenMillis
                                    )
                                    AppDatePicker(
                                        text = "Date Close",
                                        onDateSelected = { dateCloseMillis = it },
                                        selectedDateMillis = dateCloseMillis
                                    )
                                }
                            }
                        } else if (selectedTab == "Incident Type") {
                            val incidentTypes = IncidentType.entries
                            item {
                                incidentTypes.forEach { type ->
                                     Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempIncidentTypes = if (tempIncidentTypes.contains(type.id)) {
                                                    tempIncidentTypes.filterNot { it == type.id }
                                                } else {
                                                    tempIncidentTypes + type.id
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val isChecked = tempIncidentTypes.contains(type.id)
                                        Text(
                                            text = type.title,
                                            style = textStyle(size = 15.sp, weight = FontWeight.Medium),
                                            color = AppColors.Black
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Image(
                                            painter = painterResource(if (isChecked) Res.drawable.ic_checkbox_on else Res.drawable.ic_checkbox_off),
                                            contentDescription = null,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
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
                    contentPadding = PaddingValues(0.dp)
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
                                selectedReportedBy = tempReportedBy,
                                selectedIncidentTypes = tempIncidentTypes,
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
