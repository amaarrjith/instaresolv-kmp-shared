package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_forget_password
import instaresolv.shared.generated.resources.forgot_password
import instaresolv.shared.generated.resources.forgot_password_description
import instaresolv.shared.generated.resources.email_id
import instaresolv.shared.generated.resources.email_placeholder
import instaresolv.shared.generated.resources.reset_password
import org.example.project.forgetpassword.ForgetPasswordViewModel
import org.example.project.typography.textStyle
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppTextField
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    onBackButtonPressed: () -> Unit,
) {
    val viewModel: ForgetPasswordViewModel = koinInject()
    val email = remember { mutableStateOf("") }
    val uiState = viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                title = { },
                navigationIcon = {
                    NavigationBackIcon(
                        onClick = {
                            onBackButtonPressed()
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(horizontal = 28.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_forget_password),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = stringResource(Res.string.forgot_password),
                    style = textStyle(
                        size = 18.sp,
                        weight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    modifier = Modifier
                        .padding(horizontal = 56.dp),
                    text = stringResource(Res.string.forgot_password_description),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(50.dp))
                AppTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    title = stringResource(Res.string.email_id),
                    placeholder = stringResource(Res.string.email_placeholder)
                )
                Spacer(modifier = Modifier.height(50.dp))
                AppPrimaryButton(
                    title = stringResource(Res.string.reset_password),
                    onClick = {
                        viewModel.forgetPassword(email.value)
                    },
                    isLoading = uiState.value.isLoading
                )
            }
            ToastHost(
                visible = uiState.value.errorMessage != null,
                message = uiState.value.errorMessage.orEmpty(),
                onDismiss = {
                    viewModel.clearError()
                },
                type = ToastType.Error
            )
            ToastHost(
                visible = uiState.value.isPasswordResetSuccess,
                message = uiState.value.successMessage.orEmpty(),
                onDismiss = {
                    viewModel.clearSuccess()
                },
                type = ToastType.Success
            )
        }
    }
}
