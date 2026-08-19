package org.example.project.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import org.example.project.data.model.InjuredEmployee
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppTextField
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.platform.LocalDensity
import org.koin.compose.koinInject

@Composable
fun AddEmployeeBlock(
    onAddEmployee: (InjuredEmployee) -> Unit,
    onError: (String) -> Unit,
    onUploadEmployeesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    val viewModel: AddEmployeeViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    var employeeCode by remember { mutableStateOf("") }
    var employeeName by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }


    Column(modifier = modifier.fillMaxWidth()) {
        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 0.dp).zIndex(1f),
            verticalAlignment = Alignment.Bottom
        ) {
            val tab0Shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            Box(
                modifier = Modifier
                    .background(Color.White, tab0Shape)
                    .border(1.dp, if (selectedTab == 0) AppColors.Primary else Color.Transparent, tab0Shape)
                    .clickable { selectedTab = 0 }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.addEmployee),
                    style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                    color = if (selectedTab == 0) AppColors.Primary else AppColors.TextGray
                )
            }
            
            Box(
                modifier = Modifier
                    .background(Color.White, tab0Shape)
                    .border(1.dp, if (selectedTab == 1) AppColors.Primary else Color.Transparent, tab0Shape)
                    .clickable { selectedTab = 1 }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(Res.string.bulkEmployeeUpload),
                    style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                    color = if (selectedTab == 1) AppColors.Primary else AppColors.TextGray
                )
            }
        }

        if (selectedTab == 0) {
            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 8.dp,
                    bottomStart = 8.dp,
                    bottomEnd = 8.dp
                ),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    var textFieldSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
                    val density = LocalDensity.current
                    val dropdownWidth = with(density) { textFieldSize.width.toDp() }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size.toSize()
                            }
                    ) {
                        AppTextField(
                            icon = null,
                            value = employeeCode,
                            onValueChange = { 
                                employeeCode = it
                                viewModel.onSearchKeyChange(it)
                            },
                            title = stringResource(Res.string.employeeCode),
                            placeholder = stringResource(Res.string.enterEmployeeCode),
                            isMandatory = true
                        )

                        AppDropdownMenu(
                            expanded = uiState.showDropdown,
                            onDismissRequest = { viewModel.hideDropdown() },
                            properties = PopupProperties(focusable = false),
                            modifier = Modifier.width(dropdownWidth).heightIn(max = 250.dp)
                        ) {
                            if (uiState.errorMessage != null) {
                                DropdownMenuItem(
                                    text = { Text(uiState.errorMessage ?: "Parsing error", style = textStyle(size = 14.sp), color = Color.Red) },
                                    onClick = {}
                                )
                            } else if (uiState.isLoading && uiState.employees.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Loading...", style = textStyle(size = 14.sp)) },
                                    onClick = {}
                                )
                            } else if (!uiState.isLoading && uiState.employees.isEmpty() && uiState.searchKey.isNotEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No employees found", style = textStyle(size = 14.sp)) },
                                    onClick = {}
                                )
                            } else {
                                uiState.employees.forEach { employee ->
                                    DropdownMenuItem(
                                        text = { 
                                            Column {
                                                Text(employee.employeeCode ?: "-", style = textStyle(size = 14.sp, weight = FontWeight.Bold))
                                                Text(employee.employeeName ?: "-", style = textStyle(size = 12.sp, color = AppColors.TextGray))
                                            }
                                        },
                                        onClick = {
                                            employeeCode = employee.employeeCode ?: ""
                                            employeeName = employee.employeeName ?: ""
                                            companyName = employee.companyName ?: ""
                                            profession = employee.profession ?: ""
                                            viewModel.hideDropdown()
                                        }
                                    )
                                }
                                if (uiState.hasMore && uiState.employees.isNotEmpty()) {
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                if (uiState.isLoading) "Loading..." else "Load More", 
                                                style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                                                color = AppColors.Primary
                                            )
                                        },
                                        onClick = { viewModel.loadMore() }
                                    )
                                }
                            }
                        }
                    }

                    AppTextField(
                        icon = null,
                        value = employeeName,
                        onValueChange = { employeeName = it; },
                        title = stringResource(Res.string.employeeName),
                        placeholder = stringResource(Res.string.enterEmployeeName),
                        isMandatory = true,
                    )

                    AppTextField(
                        icon = null,
                        value = companyName,
                        onValueChange = { companyName = it; },
                        title = stringResource(Res.string.companyName),
                        placeholder = stringResource(Res.string.enterCompanyName)
                    )

                    AppTextField(
                        icon = null,
                        value = profession,
                        onValueChange = { profession = it; },
                        title = stringResource(Res.string.profession),
                        placeholder = stringResource(Res.string.enterProfession)
                    )

                    AppBorderButton(
                        title = stringResource(Res.string.addEmployee),
                        onClick = {
                            if (employeeCode.isBlank() || employeeName.isBlank() ) {
                                onError("Please Fill Mandatory Fields")
                            } else {
                                onAddEmployee(
                                    InjuredEmployee(
                                        employeeCode = employeeCode,
                                        employeeName = employeeName,
                                        companyName = companyName,
                                        profession = profession
                                    )
                                )
                                // Clear ic_justification
                                employeeCode = ""
                                employeeName = ""
                                companyName = ""
                                profession = ""
                            }
                        }
                    )
                }
            }
        } else {
            // Bulk Upload UI Placeholder
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 8.dp,
                    bottomStart = 8.dp,
                    bottomEnd = 8.dp
                ),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5))
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AppBorderButton(
                        title = stringResource(Res.string.uploadEmployees),
                        onClick = onUploadEmployeesClick
                    )
                }
            }
        }
    }
}
