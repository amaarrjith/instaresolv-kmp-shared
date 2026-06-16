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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.R
import org.example.project.colors.AppColors
import org.example.project.register.RegisterUiState
import org.example.project.register.RegisterViewModel
import org.example.project.typography.textStyle
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppTextField
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.koin.compose.koinInject

@Composable
fun RegisterScreen(
    isLoginClicked: () -> Unit,
    isRegisterCompleted: (tempUserId: Int, email: String) -> Unit
) {
    val viewModel: RegisterViewModel = koinInject()
    val uiState = viewModel.uiState.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp)
            .padding(bottom = 70.dp)
    ) {
        RegisterScreenContent(
            isLoginClicked = {
                isLoginClicked()
            },
            isRegisterCompleted = {
                tempUserId, email ->
                isRegisterCompleted(tempUserId, email)
            },
            isTermsClicked = {},
            isPrivacyClicked = {},
            viewModel,
            uiState
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
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

@Composable
fun RegisterScreenContent(
    isLoginClicked: () -> Unit,
    isRegisterCompleted: (
            tempUserId: Int,
            email: String
            ) -> Unit,
    isTermsClicked: () -> Unit,
    isPrivacyClicked: () -> Unit,
    viewModel: RegisterViewModel,
    uiState: State<RegisterUiState>
) {
    val fullName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val designation = remember { mutableStateOf("") }
    val company = remember { mutableStateOf("") }
    val isTermsAccepted = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.value.isRegisterSuccess) {
        if (uiState.value.isRegisterSuccess) {
            isRegisterCompleted(
                uiState.value.tempUserId,
                uiState.value.email
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)

    ) {
        Spacer(modifier = Modifier.height(94.dp))
        Text(
            text = stringResource(R.string.register),
            style = textStyle(
                size = 21.sp,
                weight = FontWeight.Bold
            ),
            color = AppColors.BlackText
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row() {
            Text(
                text = stringResource(R.string.already_have_account),
                style = textStyle(
                    size = 14.sp,
                    weight = FontWeight.Normal
                ),
                color = AppColors.BlackText
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                modifier = Modifier.clickable {
                    isLoginClicked()
                },
                text = stringResource(R.string.login_now),
                style = textStyle(
                    size = 14.sp,
                    weight = FontWeight.SemiBold
                ),
                color = AppColors.Primary
            )
        }
        Spacer(modifier = Modifier.height(50.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_avatar),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(16.dp))
                AppBorderButton(
                    title = stringResource(R.string.upload_profile),
                    onClick = {

                    },
                    modifier = Modifier.width(150.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        AppTextField(
            value = fullName.value,
            onValueChange = {
                fullName.value = it
            },
            title = stringResource(R.string.full_name),
            placeholder = stringResource(R.string.full_name)
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppTextField(
            value = email.value,
            onValueChange = {
                email.value = it
            },
            title = stringResource(R.string.email_id),
            placeholder = stringResource(R.string.email_placeholder)
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppTextField(
            value = password.value,
            onValueChange = {
                password.value = it
            },
            title = stringResource(R.string.password),
            placeholder = stringResource(R.string.password_placeholder),
            isSecure = true

        )
        Spacer(modifier = Modifier.height(20.dp))
        AppTextField(
            value = confirmPassword.value,
            onValueChange = {
                confirmPassword.value = it
            },
            title = stringResource(R.string.confirm_password),
            placeholder = stringResource(R.string.confirm_password),
            isSecure = true
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppTextField(
            value = designation.value,
            onValueChange = {
                designation.value = it
            },
            title = stringResource(R.string.designation),
            placeholder = stringResource(R.string.designation)
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppTextField(
            value = company.value,
            onValueChange = {
                company.value = it
            },
            title = stringResource(R.string.company),
            placeholder = stringResource(R.string.company)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row() {
            val id = if (isTermsAccepted.value) {
                R.drawable.ic_checkbox_on
            } else {
                R.drawable.ic_checkbox_off
            }
            Image(
                painter = painterResource(id),
                contentDescription = null,
                modifier = Modifier.clickable {
                    isTermsAccepted.value = !isTermsAccepted.value
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            TermsAndPrivacyText(
                onTermsClick = {
                    isTermsClicked()

                },
                onPrivacyClick = {
                    isPrivacyClicked()
                }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AppPrimaryButton(
                title = stringResource(R.string.register),
                isLoading = uiState.value.isLoading,
                onClick = {
                    viewModel.register(
                        fullName.value,
                        email.value,
                        password.value,
                        confirmPassword.value,
                        designation.value,
                        company.value,
                        isTermsAccepted.value
                    )
                }
            )
        }


    }
}

@Composable
fun TermsAndPrivacyText(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    val normalStyle = textStyle(
        size = 12.sp,
        weight = FontWeight.Medium,
        color = AppColors.DarkGray
    ).toSpanStyle()

    val linkStyle = textStyle(
        size = 12.sp,
        weight = FontWeight.Medium,
        color = AppColors.Primary
    ).toSpanStyle()

    val annotatedText = buildAnnotatedString {

        withStyle(normalStyle) {
            append(stringResource(R.string.privacy_policy_message))
            append(" ")
            // "I've read and agree with the "
        }

        pushStringAnnotation(
            tag = "TERMS",
            annotation = "terms"
        )
        withStyle(linkStyle) {
            append(stringResource(R.string.terms_and_conditions))
            append(" ")
            // "Terms and Conditions"
        }
        pop()

        withStyle(normalStyle) {
            append(stringResource(R.string.and_the))
            append(" ")
            // " and the "
        }

        pushStringAnnotation(
            tag = "PRIVACY",
            annotation = "privacy"
        )
        withStyle(linkStyle) {
            append(stringResource(R.string.privacy_policy))
            // "Privacy Policy"
        }
        pop()

        withStyle(normalStyle) {
            append(".")

        }
    }

    ClickableText(
        text = annotatedText,
        style = TextStyle(
            fontSize = 12.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.Primary,
            textAlign = TextAlign.Start
        ),
        onClick = { offset ->

            annotatedText
                .getStringAnnotations(
                    tag = "TERMS",
                    start = offset,
                    end = offset
                )
                .firstOrNull()
                ?.let {
                    onTermsClick()
                }

            annotatedText
                .getStringAnnotations(
                    tag = "PRIVACY",
                    start = offset,
                    end = offset
                )
                .firstOrNull()
                ?.let {
                    onPrivacyClick()
                }
        }
    )
}

@Preview
@Composable
fun RegisterScreenPreview(
    showBackground: Boolean = true
) {
    RegisterScreen(
        isLoginClicked = { },
        isRegisterCompleted = { i: Int, string: String ->

        }
    )
}
