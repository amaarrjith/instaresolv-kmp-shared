package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.unit.dp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_app_login_logo
import instaresolv.shared.generated.resources.login
import instaresolv.shared.generated.resources.not_a_member
import instaresolv.shared.generated.resources.register_now
import instaresolv.shared.generated.resources.email_id
import instaresolv.shared.generated.resources.email_placeholder
import instaresolv.shared.generated.resources.password
import instaresolv.shared.generated.resources.password_placeholder
import instaresolv.shared.generated.resources.forgot_password
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.example.project.colors.AppColors
import org.example.project.login.LoginViewModel
import org.example.project.typography.textStyle
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppTextField

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import org.example.project.login.LoginUiState
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.example.project.utilites.getAppInfo
import org.koin.compose.koinInject
import androidx.compose.ui.text.style.TextAlign
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToForgetPassword: () -> Unit,
) {
    val vm: LoginViewModel = koinInject()
    val uiState by vm.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .statusBarsPadding()
            .fillMaxSize()
            .padding(horizontal = 28.dp)
            .imePadding()
            .verticalScroll(scrollState)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { 
                    focusManager.clearFocus()
                })
            }
    ) {
        LoginScreenContent(
            navigateToRegister = navigateToRegister,
            navigateToForgetPassword = navigateToForgetPassword,
            viewModel = vm,
            uiState
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            ToastHost(
                visible = uiState.errorMessage != null,
                type = ToastType.Error,
                message = uiState.errorMessage.orEmpty(),
                onDismiss = vm::clearError
            )
        }
    }
}

@Composable
fun LoginScreenContent(
    navigateToRegister: () -> Unit,
    navigateToForgetPassword: () -> Unit,
    viewModel: LoginViewModel,
    uiStatus: LoginUiState
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val appInfo = remember { getAppInfo() }
    val currentYear = remember {
        kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_app_login_logo),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(100.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Text(
                text = stringResource(Res.string.login),
                color = AppColors.BlackText,
                style = textStyle(
                    size = 21.sp,
                    weight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row() {
                Text(
                    text = stringResource(Res.string.notAMember),
                    color = AppColors.BlackText,
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Normal
                    )
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    modifier = Modifier
                        .clickable {
                            navigateToRegister()
                        },
                    text = stringResource(Res.string.registerNow),
                    color = AppColors.Primary,
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Medium
                    )
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
            AppTextField(
                value = email.value,
                onValueChange = { email.value = it
                                viewModel.updateEmail(email.value)
                                },
                title = stringResource(Res.string.emailId),
                placeholder = stringResource(Res.string.emailPlaceholder)
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppTextField(
                value = password.value,
                onValueChange = { password.value = it
                    viewModel.updatePassword(password.value)},
                title = stringResource(Res.string.password),
                placeholder = stringResource(Res.string.passwordPlaceholder),
                isSecure = true
            )
            Spacer(modifier = Modifier.height(77.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AppPrimaryButton(
                    title = stringResource(Res.string.login),
                    onClick = {
                        viewModel.login()
                    },
                    isLoading = uiStatus.isLoading
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.clickable {
                        navigateToForgetPassword()
                    },
                    text = stringResource(Res.string.forgotPassword),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Medium
                    ),
                    color = AppColors.Primary
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = appInfo.formattedVersionInfo,
                    style = textStyle(
                        size = 10.sp,
                        weight = FontWeight.Normal
                    ),
                    color = AppColors.TextGray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(Res.string.copyrightText, currentYear),
                    style = textStyle(
                        size = 10.sp,
                        weight = FontWeight.Normal
                    ),
                    color = AppColors.TextGray,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
