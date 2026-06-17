package org.example.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.verify_otp
import instaresolv.shared.generated.resources.verify_otp_message
import instaresolv.shared.generated.resources.resend_code
import instaresolv.shared.generated.resources.continue_text
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.otp.OTPVerificationViewModel
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.NavigationBackIcon
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPVerificationScreen(
    onOTPVerified: () -> Unit,
    backButtonPressed: () -> Unit,
    tempUserId: Int = 0,
    email: String = ""
){
    val viewModel: OTPVerificationViewModel = koinInject(
        parameters = {
            parametersOf(email, tempUserId)
        }
    )
    val uiState = viewModel.uiState.collectAsState()
    var otp by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    LaunchedEffect(uiState.value.isOTPVerified) {
        if (uiState.value.isOTPVerified) {
            onOTPVerified()
        }
    }
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
                            backButtonPressed()
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(scrollState),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(Res.string.verify_otp),
                        style = textStyle(
                            size = 18.sp,
                            weight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = stringResource(Res.string.verify_otp_message),
                        textAlign = TextAlign.Center,
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Normal
                        )
                    )
                    Spacer(modifier = Modifier.height(64.dp))
                    OtpInput(
                        otp = otp,
                        onOtpChange = { otp = it }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = stringResource(Res.string.resend_code),
                        textAlign = TextAlign.Center,
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Normal
                        )
                    )
                    Spacer(modifier = Modifier.height(74.dp))
                    AppPrimaryButton(
                        title = stringResource(Res.string.continue_text),
                        isLoading = uiState.value.isLoading,
                        onClick = {
                            viewModel.verifyOTP(otp)
                        }
                    )

                }

                ToastHost(
                    visible = uiState.value.errorMessage != null,
                    type = ToastType.Error,
                    message = uiState.value.errorMessage.orEmpty(),
                ) {
                    viewModel.clearError()
                }
            }
        }
    }
}

@Composable
fun OtpInput(
    otp: String,
    otpLength: Int = 4,
    onOtpChange: (String) -> Unit
) {
    BasicTextField(
        value = otp,
        onValueChange = {
            if (it.length <= otpLength && it.all(Char::isDigit)) {
                onOtpChange(it)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword
        ),
        decorationBox = { innerTextField ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(otpLength) { index ->
                    val char = otp.getOrNull(index)?.toString() ?: ""
                    val isFocused = index == otp.length

                    Box(
                        modifier = Modifier
                            .size(width = 52.dp, height = 52.dp)
                            .border(
                                width = if (isFocused) 1.5.dp else 0.dp,
                                color = if (isFocused) AppColors.Primary else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(
                                Color(0xFFF2F2F2),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Box(modifier = Modifier.size(0.dp)) {
                    innerTextField()
                }
            }
        }
    )
}
