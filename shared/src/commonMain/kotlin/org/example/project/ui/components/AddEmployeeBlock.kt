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

@Composable
fun AddEmployeeBlock(
    onAddEmployee: (InjuredEmployee) -> Unit,
    onError: (String) -> Unit,
    isProjectSelected: Boolean,
    onUploadEmployeesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    
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
                    text = "Add Employee",
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
                    text = "Bulk Employee Upload",
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

                    // For now using simple text fields since we don't have dropdown data
                    AppTextField(
                        icon = null,
                        value = employeeCode,
                        onValueChange = { employeeCode = it; },
                        title = "Employee Code",
                        placeholder = "Enter Employee Code",
                        isMandatory = true
                    )

                    AppTextField(
                        icon = null,
                        value = employeeName,
                        onValueChange = { employeeName = it; },
                        title = "Employee Name",
                        placeholder = "Enter Employee Name",
                        isMandatory = true,
                    )

                    AppTextField(
                        icon = null,
                        value = companyName,
                        onValueChange = { companyName = it; },
                        title = "Company Name",
                        placeholder = "Enter Company Name"
                    )

                    AppTextField(
                        icon = null,
                        value = profession,
                        onValueChange = { profession = it; },
                        title = "Profession",
                        placeholder = "Enter Profession"
                    )

                    AppBorderButton(
                        title = "Add Employee",
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
                                // Clear form
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
                    if (isProjectSelected) {
                        AppBorderButton(
                            title = "Upload Employees",
                            onClick = onUploadEmployeesClick
                        )
                    } else {
                        Text(
                            text = "Please select a project first",
                            style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                            color = AppColors.Primary
                        )
                    }
                }
            }
        }
    }
}
