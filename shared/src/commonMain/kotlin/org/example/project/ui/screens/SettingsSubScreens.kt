package org.example.project.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.utilites.AppTextField
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_arrow_left
import instaresolv.shared.generated.resources.ic_app_logo
import instaresolv.shared.generated.resources.ic_forget_password
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.settings.GeneralContentsViewModel
import org.example.project.settings.GeneralContentsUiState
import org.example.project.ui.components.AppLoader
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import instaresolv.shared.generated.resources.ic_location
import instaresolv.shared.generated.resources.ic_sms
import instaresolv.shared.generated.resources.ic_trash
import org.example.project.utilites.AppPrimaryButton
import org.example.project.ui.components.AppSuccessDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericSettingsScreen(title: String, type: Int?, showAppIcon: Boolean = false, onBack: () -> Unit) {
    val viewModel: GeneralContentsViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(type) {
        if (type != null) {
            viewModel.fetchContent(type)
        }
    }
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        text = title.uppercase(),
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Bold
                        ),
                        color = AppColors.Black
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(16.dp),
                        tint = AppColors.Black
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (type == null) {
                Text(title, color = AppColors.Black)
            } else {
                when (uiState) {
                    is GeneralContentsUiState.Idle -> {}
                    is GeneralContentsUiState.Loading -> {
                        AppLoader()
                    }
                    is GeneralContentsUiState.Success -> {
                        val content = (uiState as GeneralContentsUiState.Success).content
                        
                        // Basic decoding of the double-encoded HTML payload to plain text
                        val decoded = content
                            .replace("&lt;", "<")
                            .replace("&gt;", ">")
                            .replace("&ndash;", "-")
                            .replace("&amp;", "&")
                        
                        // Strip html tags
                        val plainText = decoded.replace(Regex("<.*?>"), "").replace("\r\n", "\n")

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (showAppIcon) {
                                Image(
                                    painter = painterResource(Res.drawable.ic_app_logo),
                                    contentDescription = "App Logo",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(bottom = 24.dp)
                                )
                            }
                            Text(
                                text = plainText,
                                color = AppColors.Black,
                                style = textStyle(size = 14.sp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    is GeneralContentsUiState.Error -> {
                        val error = (uiState as GeneralContentsUiState.Error).message
                        Text(
                            text = error,
                            color = Color.Red,
                            style = textStyle(size = 14.sp),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(onBack: () -> Unit) {
    val viewModel: org.example.project.settings.ChangePasswordViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var toastType by remember { mutableStateOf(ToastType.Error) }
    var showSuccessDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is org.example.project.settings.ChangePasswordUiState.Success -> {
                showSuccessDialog = state.message
                viewModel.resetState()
            }
            is org.example.project.settings.ChangePasswordUiState.Error -> {
                toastMessage = state.message
                toastType = ToastType.Error
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        text = "CHANGE PASSWORD",
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Bold
                        ),
                        color = AppColors.Black
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(16.dp),
                        tint = AppColors.Black
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 22.dp)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Image(
                    painter = painterResource(Res.drawable.ic_forget_password),
                    contentDescription = "Change Password",
                    modifier = Modifier.size(150.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    title = "Old Password",
                    placeholder = "Old Password",
                    isSecure = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    title = "New Password",
                    placeholder = "New Password",
                    isSecure = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    title = "Confirm New Password",
                    placeholder = "Confirm New Password",
                    isSecure = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppPrimaryButton(
                    title = "Submit",
                    onClick = {
                        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                            toastMessage = "Please fill all fields"
                            toastType = ToastType.Error
                        } else if (newPassword != confirmPassword) {
                            toastMessage = "New passwords do not match"
                            toastType = ToastType.Error
                        } else {
                            viewModel.changePassword(oldPassword, newPassword)
                        }
                    },
                    isLoading = uiState is org.example.project.settings.ChangePasswordUiState.Loading
                )
            }
        ToastHost(
            visible = toastMessage != null,
            message = toastMessage ?: "",
            onDismiss = { toastMessage = null },
            type = toastType
        )

        org.example.project.ui.components.AppSuccessDialog(
            visible = showSuccessDialog != null,
            title = "Success",
            description = showSuccessDialog ?: "",
            onDismiss = {
                showSuccessDialog = null
                onBack()
            }
        )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(onBack: () -> Unit) {
    val viewModel: org.example.project.settings.ContactUsViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var fullName by remember { mutableStateOf("") }
    var emailId by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    var toastMessage by remember { mutableStateOf<String?>(null) }
    var toastType by remember { mutableStateOf(ToastType.Error) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            toastMessage = uiState.error
            toastType = ToastType.Error
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        text = "CONTACT US",
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Bold
                        ),
                        color = AppColors.Black
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(16.dp),
                        tint = AppColors.Black
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Contact Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(Res.drawable.ic_sms),
                        contentDescription = "Location",
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "connect@instaresolv.com",
                        style = textStyle(size = 14.sp),
                        color = Color(0xFF334155) // Slate gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.Top) {
                    Image(
                        painter = painterResource(Res.drawable.ic_location),
                        contentDescription = "Location",
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "33 Level, Al Saqr Business Tower,\nSheikh Zayed Road, Dubai",
                        style = textStyle(size = 14.sp),
                        color = Color(0xFF334155)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Message",
                    style = textStyle(
                        size = 18.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Form
                AppTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    title = "Full Name",
                    placeholder = "Full Name"
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = emailId,
                    onValueChange = { emailId = it },
                    title = "Email ID",
                    placeholder = "Email ID"
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = message,
                    onValueChange = { message = it },
                    title = "Message *",
                    placeholder = "Message",
                    singleLine = false,
                    textFieldModifier = Modifier.fillMaxWidth().height(120.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                AppPrimaryButton(
                    title = "Submit",
                    onClick = { viewModel.sendMessage(name = fullName, email = emailId, message = message) },
                    isLoading = uiState.isLoading
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            ToastHost(
                visible = toastMessage != null,
                message = toastMessage ?: "",
                onDismiss = { toastMessage = null },
                type = toastType
            )

            org.example.project.ui.components.AppSuccessDialog(
                visible = uiState.isSuccess,
                title = "Success",
                description = "Your message has been sent successfully.",
                onDismiss = {
                    viewModel.resetSuccess()
                    onBack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        text = "ABOUT US",
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Bold
                        ),
                        color = AppColors.Black
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(16.dp),
                        tint = AppColors.Black
                    )
                }
            )
        }
    ) { paddingValues ->
        org.example.project.utilites.AppWebView(
            url = "https://instaresolv-dev.zoondia.org/web/about-us",
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        )
    }
}

@Composable
fun TermsOfUseScreen(onBack: () -> Unit) {
    GenericSettingsScreen("Terms of Use", 1, showAppIcon = true) { onBack() }
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    GenericSettingsScreen("Privacy Policy", 2, showAppIcon = true) { onBack() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(onBack: () -> Unit) {
    val viewModel: org.example.project.settings.DeleteAccountViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var password by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var toastType by remember { mutableStateOf(ToastType.Error) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            toastMessage = uiState.error
            toastType = ToastType.Error
            viewModel.clearError()
        }
    }

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AppSuccessDialog(
            visible = true,
            title = "Success",
            description = "Account deleted successfully.",
            buttonText = "Close",
            onDismiss = {
                showSuccessDialog = false
                onBack()
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                title = {
                    Text(
                        text = "DELETE ACCOUNT",
                        style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                        color = AppColors.Black
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(16.dp),
                        tint = AppColors.Black
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 22.dp)
                .imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Image(
                    painter = painterResource(Res.drawable.ic_trash),
                    contentDescription = "Delete Account",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (uiState.terms != null) {
                    Text(
                        text = uiState.terms!!,
                        style = textStyle(size = 14.sp, weight = FontWeight.Normal),
                        color = AppColors.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (uiState.step == org.example.project.settings.DeleteAccountStep.TERMS_LOADED) {
                        AppTextField(
                            value = password,
                            onValueChange = { password = it },
                            title = "Password",
                            placeholder = "Enter your password",
                            isSecure = true
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        AppPrimaryButton(
                            title = "Confirm",
                            onClick = {
                                if (password.isNotEmpty()) {
                                    viewModel.requestDelete(password)
                                } else {
                                    toastMessage = "Please enter your password"
                                    toastType = ToastType.Error
                                }
                            },
                            isLoading = uiState.isLoading
                        )
                    } else if (uiState.step == org.example.project.settings.DeleteAccountStep.OTP_SENT) {
                        AppTextField(
                            value = otp,
                            onValueChange = { otp = it },
                            title = "OTP",
                            placeholder = "Enter OTP sent to email"
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        AppPrimaryButton(
                            title = "Submit",
                            onClick = {
                                if (otp.isNotEmpty()) {
                                    viewModel.verifyDelete(password, otp)
                                } else {
                                    toastMessage = "Please enter OTP"
                                    toastType = ToastType.Error
                                }
                            },
                            isLoading = uiState.isLoading
                        )
                    }
                }
            }

            if (uiState.isLoading && uiState.step == org.example.project.settings.DeleteAccountStep.FETCHING_TERMS) {
                AppLoader()
            }

            ToastHost(
                visible = toastMessage != null,
                message = toastMessage ?: "",
                onDismiss = { toastMessage = null },
                type = toastType
            )
        }
    }
}
