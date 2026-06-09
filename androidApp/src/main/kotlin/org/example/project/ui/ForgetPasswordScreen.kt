package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.R
import org.example.project.typography.textStyle
import org.example.project.utilites.AppPrimaryButton
import org.example.project.utilites.AppTextField
import org.example.project.utilites.NavigationBackIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    onBackButtonPressed: () -> Unit,
) {
    val email = remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
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
                .padding(paddingValues)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Image(
                    painter = painterResource(R.drawable.ic_forget_password),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = stringResource(R.string.forgot_password),
                    style = textStyle(
                        size = 18.sp,
                        weight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    modifier = Modifier
                        .padding(horizontal = 56.dp),
                    text = stringResource(R.string.forgot_password_description),
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
                    title = stringResource(R.string.email_id),
                    placeholder = stringResource(R.string.email_placeholder)
                )
                Spacer(modifier = Modifier.height(50.dp))
                AppPrimaryButton(
                    title = stringResource(R.string.reset_password),
                    onClick = {

                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun ForgetPasswordScreenPreview(
    showBackground: Boolean = true
) {
    ForgetPasswordScreen(
        {}
    )
}
