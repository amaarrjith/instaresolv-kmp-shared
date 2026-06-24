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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
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
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.example.project.localization.LocalAppStrings
import org.example.project.ui.viewmodel.GlobalSettingsViewModel
import androidx.compose.runtime.collectAsState
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onChangePasswordClick: () -> Unit = {},
    onContactUsClick: () -> Unit = {},
    onAboutUsClick: () -> Unit = {},
    onTermsOfUseClick: () -> Unit = {},
    onPrivacyPolicyClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {}
) {
    val globalSettingsViewModel: GlobalSettingsViewModel = koinInject()
    val currentLanguage by globalSettingsViewModel.currentLanguage.collectAsState()
    var isAppNotificationEnabled by remember { mutableStateOf(false) }
    var isEmailNotificationEnabled by remember { mutableStateOf(false) }
    var showLanguageSheet by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val strings = LocalAppStrings.current
    
    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row (
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(vertical = 20.dp),
            ) {
                Text(
                    text = strings.settings.uppercase(),
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
                item { SettingsRow(title = strings.appLanguage, subText = currentLanguage.title, action = { showLanguageSheet = true }) }
                item { SettingsRow(title = strings.changePassword, subText = "********", action = { onChangePasswordClick() }) }
                item { 
                    SettingsRow(
                        title = "App Notifications", 
                        subText = "Enable App Notifications", 
                        checked = isAppNotificationEnabled, 
                        isToggle = true, 
                        onCheckedChange = { 
                            isAppNotificationEnabled = it 
                            toastMessage = if (it) "App Notifications Enabled" else "App Notifications Disabled"
                        }
                    ) 
                }
                item { 
                    SettingsRow(
                        title = "Email Notifications", 
                        subText = "Enable Email Notifications", 
                        checked = isEmailNotificationEnabled, 
                        isToggle = true, 
                        onCheckedChange = { 
                            isEmailNotificationEnabled = it 
                            toastMessage = if (it) "Email Notifications Enabled" else "Email Notifications Disabled"
                        }
                    ) 
                }
                item { SettingsRow(title = strings.contactUs, subText = "zoondia@gmail.com", action = { onContactUsClick() }) }
                item { SettingsRow(title = "About Us", subText = "Learn about the app", action = { onAboutUsClick() }) }
                item { SettingsRow(title = strings.termsAndConditions, subText = "Read Terms", action = { onTermsOfUseClick() }) }
                item { SettingsRow(title = strings.privacyPolicy, subText = "Read Privacy", action = { onPrivacyPolicyClick() }) }
                item { SettingsRow(title = strings.deleteAccount, subText = "We will miss you!", titleColor = AppColors.Error, action = { onDeleteAccountClick() }) }
            }

            ToastHost(
                visible = toastMessage != null,
                message = toastMessage ?: "",
                onDismiss = { toastMessage = null },
                type = ToastType.Success
            )
        }
    }

    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showLanguageSheet = false
            },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = strings.selectLanguage,
                    style = textStyle(
                        size = 16.sp,
                        weight = FontWeight.SemiBold
                    ),
                    color = AppColors.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppLanguage.entries.forEach { lang ->
                    val isSelected = lang == currentLanguage
                    SettingsRow(
                        title = lang.title,
                        subText = lang.title,
                        leadingIcon = {
                            Text(
                                text = lang.flagEmoji,
                                fontSize = 24.sp
                            )
                        },
                        action = {
                            globalSettingsViewModel.changeLanguage(lang)
                            showLanguageSheet = false
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
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
    leadingIcon: @Composable (() -> Unit)? = null,
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
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(16.dp))
            }
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

enum class AppLanguage(val code: String, val title: String, val flagEmoji: String, val isRtl: Boolean) {
    ENGLISH("en", "English", "🇬🇧", false),
    ARABIC("ar", "Arabic", "🇸🇦", true),
    URDU("ur", "Urdu", "🇵🇰", true),
    HINDI("hi", "Hindi", "🇮🇳", false),
    SPANISH("es", "Spanish", "🇪🇸", false)
}