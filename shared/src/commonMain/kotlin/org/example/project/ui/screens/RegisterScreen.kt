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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import org.example.project.ui.components.imagepicker.AppImagePicker
import org.example.project.ui.components.WebImageView
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_avatar
import instaresolv.shared.generated.resources.ic_checkbox_on
import instaresolv.shared.generated.resources.ic_checkbox_off
import instaresolv.shared.generated.resources.register
import instaresolv.shared.generated.resources.already_have_account
import instaresolv.shared.generated.resources.login_now
import instaresolv.shared.generated.resources.upload_profile
import instaresolv.shared.generated.resources.ic_camera
import instaresolv.shared.generated.resources.full_name
import instaresolv.shared.generated.resources.email_id
import instaresolv.shared.generated.resources.email_placeholder
import instaresolv.shared.generated.resources.password
import instaresolv.shared.generated.resources.password_placeholder
import instaresolv.shared.generated.resources.confirm_password
import instaresolv.shared.generated.resources.designation
import instaresolv.shared.generated.resources.company
import instaresolv.shared.generated.resources.privacy_policy_message
import instaresolv.shared.generated.resources.terms_and_conditions
import instaresolv.shared.generated.resources.and_the
import instaresolv.shared.generated.resources.privacy_policy
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
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@Composable
fun RegisterScreen(
    isLoginClicked: () -> Unit,
    isRegisterCompleted: (tempUserId: Int, email: String) -> Unit,
    isTermsClicked: () -> Unit = {},
    isPrivacyClicked: () -> Unit = {}
) {
    val viewModel: RegisterViewModel = koinInject()
    val uiState = viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp)
            .padding(bottom = 10.dp)
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(scrollState)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        RegisterScreenContent(
            isLoginClicked = {
                isLoginClicked()
            },
            isRegisterCompleted = {
                tempUserId, email ->
                isRegisterCompleted(tempUserId, email)
            },
            isTermsClicked = { isTermsClicked() },
            isPrivacyClicked = { isPrivacyClicked() },
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
    val showPicker = remember { mutableStateOf(false) }
    val profileImageUrl = remember { mutableStateOf("") }
    val isUploadingImage = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.value.isRegisterSuccess) {
        if (uiState.value.isRegisterSuccess) {
            isRegisterCompleted(
                uiState.value.tempUserId,
                uiState.value.email
            )
        }
    }

    AppImagePicker(
        showPicker = showPicker,
        imageType = 1,
        showFullScreenLoader = false,
        onIsUploading = { isUploading ->
            isUploadingImage.value = isUploading
        },
        onImageUploaded = { url ->
            profileImageUrl.value = url
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(94.dp))
        Text(
            text = stringResource(Res.string.register),
            style = textStyle(
                size = 21.sp,
                weight = FontWeight.Bold
            ),
            color = AppColors.BlackText
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row() {
            Text(
                text = stringResource(Res.string.alreadyHaveAccount),
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
                text = stringResource(Res.string.loginNow),
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
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            showPicker.value = true
                        }
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUrl.value.isNotEmpty()) {
                        WebImageView(
                            imageUrl = profileImageUrl.value,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(Res.drawable.ic_avatar),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (isUploadingImage.value) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = AppColors.Primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_camera),
                        contentDescription = stringResource(Res.string.uploadProfile),
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        AppTextField(
            value = fullName.value,
            onValueChange = {
                fullName.value = it
            },
            title = stringResource(Res.string.fullName),
            placeholder = stringResource(Res.string.fullName),
            isMandatory = true
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppTextField(
            value = email.value,
            onValueChange = {
                email.value = it
            },
            title = stringResource(Res.string.emailId),
            placeholder = stringResource(Res.string.emailPlaceholder),
            isMandatory = true
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppTextField(
            value = password.value,
            onValueChange = {
                password.value = it
            },
            title = stringResource(Res.string.password),
            placeholder = stringResource(Res.string.passwordPlaceholder),
            isSecure = true,
            isMandatory = true

        )
        Spacer(modifier = Modifier.height(20.dp))
        AppTextField(
            value = confirmPassword.value,
            onValueChange = {
                confirmPassword.value = it
            },
            title = stringResource(Res.string.confirmPassword),
            placeholder = stringResource(Res.string.confirmPassword),
            isSecure = true,
            isMandatory = true
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppTextField(
            value = designation.value,
            onValueChange = {
                designation.value = it
            },
            title = stringResource(Res.string.designation),
            placeholder = stringResource(Res.string.designation)
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppTextField(
            value = company.value,
            onValueChange = {
                company.value = it
            },
            title = stringResource(Res.string.company),
            placeholder = stringResource(Res.string.company)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            val drawable = if (isTermsAccepted.value) {
                Res.drawable.ic_checkbox_on
            } else {
                Res.drawable.ic_checkbox_off
            }
            Image(
                painter = painterResource(drawable),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                    isTermsAccepted.value = !isTermsAccepted.value
                    }
                    .size(24.dp)
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
                title = stringResource(Res.string.register),
                isLoading = uiState.value.isLoading,
                onClick = {
                    viewModel.register(
                        profileImageUrl.value,
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
        Spacer(modifier = Modifier.height(10.dp))
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
            append(stringResource(Res.string.privacyPolicyMessage))
            append(" ")
        }

        pushStringAnnotation(
            tag = "TERMS",
            annotation = "terms"
        )
        withStyle(linkStyle) {
            append(stringResource(Res.string.termsAndConditions))
            append(" ")
        }
        pop()

        withStyle(normalStyle) {
            append(stringResource(Res.string.andThe))
            append(" ")
        }

        pushStringAnnotation(
            tag = "PRIVACY",
            annotation = "privacy"
        )
        withStyle(linkStyle) {
            append(stringResource(Res.string.privacyPolicy))
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
