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
import org.example.project.ui.screens.PermitToWorkListViewModel

import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppFilterBottomSheet(
    appliedFilterState: AppFilterState,
    isFromObservation: Boolean = false,
    isFromIncident: Boolean = false,
    isFromPermit: Boolean = false,
    moduleName: String = "Observations",
    onApply: (AppFilterState) -> Unit,
    onDismiss: () -> Unit
) {
    val viewModel: FilterBottomSheetViewModel = koinInject()
    val permitViewModel: PermitToWorkListViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()
    val permitUiState by permitViewModel.uiState.collectAsState()
    val filterData = uiState.filterData

    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    val tabs = remember(isFromObservation, isFromIncident, isFromPermit) {
        val baseTabs = mutableListOf("Project", "Reported By", "Date")
        if (isFromObservation) {
            baseTabs.add(0, "Status")
            baseTabs.add(2, "Observer")
            baseTabs.add(3, "Responsible")
            baseTabs.remove("Reported By")
        }
        if (isFromIncident) {
            baseTabs.add("Incident Type")
        }
        if (isFromPermit) {
            baseTabs.add(1, "Authorizer")
            baseTabs.add(2, "Requestor")
            baseTabs.add(3, "HSE Assigned")
            baseTabs.add(4, "Permit Type")
            baseTabs.add(6, "Validity")
            baseTabs.remove("Reported By")
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
    var tempAuthorizers by remember { mutableStateOf(appliedFilterState.selectedAuthorizers) }
    var tempRequestors by remember { mutableStateOf(appliedFilterState.selectedRequestors) }
    var tempHseAssigned by remember { mutableStateOf(appliedFilterState.selectedHseAssigned) }
    var tempPermitTypes by remember { mutableStateOf(appliedFilterState.selectedPermitTypes) }
    var tempValidity by remember { mutableStateOf(appliedFilterState.selectedValidity) }

    LaunchedEffect(Unit) {
        if (isFromPermit) {
            permitViewModel.fetchPermitTypes()
        }
    }

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
                            "Authorizer" -> tempAuthorizers.isNotEmpty()
                            "Requestor" -> tempRequestors.isNotEmpty()
                            "HSE Assigned" -> tempHseAssigned.isNotEmpty()
                            "Permit Type" -> tempPermitTypes.isNotEmpty()
                            "Validity" -> tempValidity != null
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
                            text = stringResource(Res.string.clearAll),
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
                                tempAuthorizers = emptyList()
                                tempRequestors = emptyList()
                                tempHseAssigned = emptyList()
                                tempPermitTypes = emptyList()
                                tempValidity = null
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
                                            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
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
                            if (!isFromPermit) {
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
                                                text = stringResource(Res.string.notSpecified),
                                                style = textStyle(
                                                    size = 12.sp,
                                                    weight = FontWeight.Medium
                                                ),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = stringResource(Res.string.showAllItemsThatAreNotSpecifiedToAnyProject),
                                                style = textStyle(
                                                    size = 10.sp,
                                                    weight = FontWeight.Normal
                                                ),
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
                            }
                            if (projects.isEmpty()) {

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
                                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
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
                                                    style = textStyle(size = 8.sp, weight = FontWeight.Medium),
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
                                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                            val observers = filterData?.responsiblePersons ?: emptyList() // Fixed to use observers instead of responsiblePersons
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
                                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                        text = stringResource(Res.string.dateOpen),
                                        onDateSelected = { dateOpenMillis = it },
                                        selectedDateMillis = dateOpenMillis
                                    )
                                    AppDatePicker(
                                        text = stringResource(Res.string.dateClose),
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
                                            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
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
                        } else if (selectedTab == "Authorizer") {
                            val authorizers = filterData?.responsiblePersons?.filter { it.designation.contains(1) }
                                ?: emptyList()
                            if (authorizers.isEmpty()) {
                                item { EmptyScreenView("No Authorizer Found") }
                            } else {
                                items(authorizers.size) { index ->
                                    val authorizer = authorizers[index]
                                    val isChecked = tempAuthorizers.any { it.userId == authorizer.userId }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempAuthorizers = if (isChecked) {
                                                    tempAuthorizers.filterNot { it.userId == authorizer.userId }
                                                } else {
                                                    tempAuthorizers + authorizer
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        WebImageView(
                                            imageUrl = authorizer.image,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF2F2F2))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = authorizer.name,
                                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = authorizer.email,
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
                        } else if (selectedTab == "Requestor") {
                            val requestors = filterData?.responsiblePersons?.filter { it.designation.contains(2) } ?: emptyList()
                            if (requestors.isEmpty()) {
                                item { EmptyScreenView("No Requestor Found") }
                            } else {
                                items(requestors.size) { index ->
                                    val requestor = requestors[index]
                                    val isChecked = tempRequestors.any { it.userId == requestor.userId }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempRequestors = if (isChecked) {
                                                    tempRequestors.filterNot { it.userId == requestor.userId }
                                                } else {
                                                    tempRequestors + requestor
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        WebImageView(
                                            imageUrl = requestor.image,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF2F2F2))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = requestor.name,
                                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = requestor.email,
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
                        } else if (selectedTab == "HSE Assigned") {
                            val hseAssignedList = filterData?.responsiblePersons?.filter { it.designation.contains(4) } ?: emptyList()
                            if (hseAssignedList.isEmpty()) {
                                item { EmptyScreenView("No HSE Assigned Person Found") }
                            } else {
                                items(hseAssignedList.size) { index ->
                                    val hsePerson = hseAssignedList[index]
                                    val isChecked = tempHseAssigned.any { it.userId == hsePerson.userId }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempHseAssigned = if (isChecked) {
                                                    tempHseAssigned.filterNot { it.userId == hsePerson.userId }
                                                } else {
                                                    tempHseAssigned + hsePerson
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        WebImageView(
                                            imageUrl = hsePerson.image,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFF2F2F2))
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = hsePerson.name,
                                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = hsePerson.email,
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
                        } else if (selectedTab == "Permit Type") {
                            val permitTypes = permitUiState.permitTypesList
                            if (permitTypes.isEmpty()) {
                                item { EmptyScreenView("No Permit Type Found") }
                            } else {
                                items(permitTypes.size) { index ->
                                    val type = permitTypes[index]
                                    val isChecked = tempPermitTypes.any { it.permitTypeId == type.permitTypeId }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempPermitTypes = if (isChecked) {
                                                    tempPermitTypes.filterNot { it.permitTypeId == type.permitTypeId }
                                                } else {
                                                    tempPermitTypes + type
                                                }
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val image = type.image
                                        if (!image.isNullOrEmpty()) {
                                            WebImageView(
                                                imageUrl = image,
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFF2F2F2))
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = type.permitTypeTitle ?: "",
                                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                                color = AppColors.Black
                                            )
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
                        } else if (selectedTab == "Validity") {
                            val validityOptions = listOf(
                                Pair("Valid", 1),
                                Pair("Expired", 2)
                            )
                            item {
                                validityOptions.forEach { option ->
                                    val isChecked = tempValidity == option.second
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                tempValidity = if (isChecked) null else option.second
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = option.first,
                                            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
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
                                dateCloseMillis = dateCloseMillis,
                                selectedAuthorizers = tempAuthorizers,
                                selectedRequestors = tempRequestors,
                                selectedHseAssigned = tempHseAssigned,
                                selectedPermitTypes = tempPermitTypes,
                                selectedValidity = tempValidity
                            )
                        )
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AppColors.Primary)
                ) {
                    Text(
                        text = stringResource(Res.string.applyFilter),
                        color = Color.White,
                        style = textStyle(size = 14.sp, weight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}
