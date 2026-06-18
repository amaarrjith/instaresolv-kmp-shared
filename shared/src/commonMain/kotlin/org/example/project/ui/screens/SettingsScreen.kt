package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_arrow_left
import instaresolv.shared.generated.resources.ic_right_icon
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.painterResource

@Composable
fun SettingsScreen() {
    var isAppNotificationEnabled by remember { mutableStateOf(false) }
    var isEmailNotificationEnabled by remember { mutableStateOf(false) }
    val items = listOf(
        SettingsItem(
            title = "Company Details",
            subText = "Change Company Details",
            action = {}
        ),
        SettingsItem(
            title = "Company Details",
            subText = "Change Company Details",
            action = {}
        )
    )
    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row (
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(vertical = 20.dp),
            ) {
                Text(
                    text = "Settings".uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .padding(paddingValues)
                .padding(horizontal = 28.dp)
        ) {
            LazyColumn {
                item { SettingsRow(title = "Company Details", subText = "Change Company Details") }
                item { SettingsRow(title = "App Language", subText = "English") {} }
                item { SettingsRow(title = "Change Password", subText = "********") {} }
                item { SettingsRow(title = "App Notifications", subText = "Enable App Notifications", checked = isAppNotificationEnabled, isToggle = true, onCheckedChange = { isAppNotificationEnabled = it }) }
                item { SettingsRow(title = "Email Notifications", subText = "Enable Email Notifications", checked = isEmailNotificationEnabled, isToggle = true, onCheckedChange = { isEmailNotificationEnabled = it }) }
                item { SettingsRow(title = "About Us", subText = "About InstaResolv") {} }
                item { SettingsRow(title = "Connect Us", subText = "Connect InstaResolv Team") {} }
                item { SettingsRow(title = "Terms of Use & Privacy Policy", subText = "About InstaResolv") {} }
                item { SettingsRow(title = "Delete Account", subText = "Permanently Delete Account", titleColor = AppColors.Primary) {} }
            }
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    subText: String,
    checked: Boolean = false,
    titleColor: Color = AppColors.Black,
    isToggle: Boolean = false,
    action: () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { action() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(horizontal = 14.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.SemiBold
                    ),
                    color = titleColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subText,
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.Medium
                    ),
                    color = AppColors.TextGray
                )
            }
            if (isToggle) {
                Switch(
                    checked = checked,
                    onCheckedChange = { it ->
                        onCheckedChange(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppColors.Primary,

                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray,

                        checkedBorderColor = AppColors.Primary,
                        uncheckedBorderColor = Color.LightGray
                    )
                )
            } else {
                Image(
//                modifier = Modifier
//                    .rotate(180f),
                    painter = painterResource(Res.drawable.ic_right_icon),
                    contentDescription = null,
                )
            }
        }
        HorizontalDivider(
            thickness = 0.5.dp,
            color = AppColors.TextGray
        )
    }
}

data class SettingsItem(
    val title: String,
    val subText: String,
    val action: () -> Unit
)