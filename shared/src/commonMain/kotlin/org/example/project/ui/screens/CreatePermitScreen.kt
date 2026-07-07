package org.example.project.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.ui.components.AppDatePicker
import org.example.project.ui.components.AppProjectDropdown
import org.example.project.utilites.AppTextField
import org.example.project.utilites.NavigationBackIcon

@Composable
fun CreatePermitScreen(
    onBackClicked: () -> Unit = {},
    permitTypeId: Int = -1,
    permitTypeName: String = ""
) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationBackIcon(
                    onClick = {
                        onBackClicked()
                    }
                )
                Text(
                    text = "Create Permit".uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
                .padding(horizontal = 22.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                if (permitTypeName.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AppColors.Primary.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Permit Type:",
                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                color = AppColors.Primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = permitTypeName,
                                style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                                color = AppColors.Primary
                            )
                        }
                    }
                }
                AppProjectDropdown(
                    title = "Specify Facility/Project",
                    placeholder = "Choose Facility",
                    selectedProject = null,
                    onProjectSelected = { },
                )
                Text(
                    text = "Permit Validity",
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.SemiBold
                    )
                )
                AppTextField(
                    value = "",
                    onValueChange = { },
                    title = "Contractor Name",
                    placeholder = "Enter Contractor Name"
                )
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Permit Date",
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppDatePicker(
                        text = "Permit Date",
                        onDateSelected = { },
                        selectedDateMillis = null
                    )
                }
            }
        }
    }
}


@Composable
@Preview
fun CreatePermitScreenPreview(){
    CreatePermitScreen()
}