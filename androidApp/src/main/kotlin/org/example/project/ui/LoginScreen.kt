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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.example.project.R
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.example.project.colors.AppColors
import org.example.project.login.LoginViewModel
import org.example.project.typography.textStyle
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppTextField

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToForgetPassword: () -> Unit,
    vm: LoginViewModel
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            vm.clearError()
        }
    }

    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LoginScreenContent(
            navigateToRegister = navigateToRegister,
            navigateToForgetPassword = navigateToForgetPassword,
            viewModel = vm
        )
    }
}

@Composable
fun LoginScreenContent(
    navigateToRegister: () -> Unit,
    navigateToForgetPassword: () -> Unit,
    viewModel: LoginViewModel
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(163.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_app_login_logo),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(104.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {
            Text(
                text = stringResource(R.string.login),
                color = AppColors.BlackText,
                style = textStyle(
                    size = 21.sp,
                    weight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row() {
                Text(
                    text = stringResource(R.string.not_a_member),
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
                    text = stringResource(R.string.register_now),
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
                title = stringResource(R.string.email_id),
                placeholder = stringResource(R.string.email_placeholder)
            )
            Spacer(modifier = Modifier.height(16.dp))
            AppTextField(
                value = password.value,
                onValueChange = { password.value = it
                    viewModel.updatePassword(password.value)},
                title = stringResource(R.string.password),
                placeholder = stringResource(R.string.password_placeholder),
                isSecure = true
            )
            Spacer(modifier = Modifier.height(77.dp))
            AppPrimaryButton(
                title = stringResource(R.string.login),
                onClick = {
                    viewModel.login()
                }
            )
            Spacer(modifier = Modifier.height(30.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.clickable {
                        navigateToForgetPassword()
                    },
                    text = stringResource(R.string.forgot_password),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Medium
                    ),
                    color = AppColors.Primary
                )
            }
        }
    }
}
