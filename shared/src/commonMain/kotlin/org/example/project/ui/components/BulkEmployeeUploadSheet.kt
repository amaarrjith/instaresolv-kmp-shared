package org.example.project.ui.components

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
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.colors.AppColors
import org.example.project.data.model.EmployeeData
import org.example.project.typography.textStyle
import org.example.project.utilites.AppTextField
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkEmployeeUploadSheet(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    employees: List<EmployeeData>,
    selectedEmployeeIds: Set<Int>,
    onToggleSelection: (Int) -> Unit,
    onSelectAll: () -> Unit,
    onAddEmployees: () -> Unit,
    onLoadMore: () -> Unit,
    isLoading: Boolean,
    hasMore: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
            .background(Color.White)
            .padding(horizontal = 16.dp),
    ) {

        AppSearchBar(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = "Search Employees",
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp) // space for bottom buttons
            ) {
                items(employees) { employee ->
                    EmployeeListItem(
                        employee = employee,
                        isSelected = selectedEmployeeIds.contains(employee.id),
                        onClick = { onToggleSelection(employee.id) }
                    )
                }
                
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = AppColors.Primary)
                        }
                    }
                } else if (hasMore && employees.isNotEmpty()) {
                    item {
                        LaunchedEffect(Unit) {
                            onLoadMore()
                        }
                    }
                }
                
                if (!isLoading && employees.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No employees found",
                                style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                                color = AppColors.TextGray
                            )
                        }
                    }
                }
            }
            
            // Bottom sticky action bar
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    AppPrimaryButton(
                        title = "Add Employees (${selectedEmployeeIds.size})",
                        onClick = onAddEmployees
                    )
                }
            }
        }
    }
}

@Composable
fun EmployeeListItem(
    employee: EmployeeData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AppColors.Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            val initial = employee.employeeName?.firstOrNull()?.uppercase() ?: "?"
            Text(
                text = initial,
                style = textStyle(size = 16.sp, weight = FontWeight.Bold),
                color = AppColors.Primary
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = employee.employeeName ?: "Unknown",
                style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                color = AppColors.Black
            )
            val details = listOfNotNull(
                employee.employeeCode,
                employee.companyName,
                employee.profession
            ).filter { it.isNotBlank() }.joinToString(" • ")
            
            if (details.isNotEmpty()) {
                Text(
                    text = details,
                    style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                    color = AppColors.TextGray
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Custom Checkbox/Radio button
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isSelected) AppColors.Primary else Color.Transparent)
                .border(
                    width = 1.dp,
                    color = if (isSelected) AppColors.Primary else AppColors.TextGray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                // inner white circle for radio button effect
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    }
}
