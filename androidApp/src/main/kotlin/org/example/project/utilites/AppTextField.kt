package org.example.project.utilites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.R
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    placeholder: String,
    isSecure: Boolean = false,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = title,
            style = textStyle(
                size = 12.sp,
                weight = FontWeight.SemiBold
            ),
            color = AppColors.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text(
                    text = placeholder,
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Medium
                    ),
                    color = Color(0xFF9E9E9E)
                )
            },
            visualTransformation =
                if (isSecure && !passwordVisible)
                    PasswordVisualTransformation()
                else
                    VisualTransformation.None,
            trailingIcon = {
                if (isSecure) {
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible)
                                    R.drawable.ic_eye_open
                                else
                                    R.drawable.ic_eye_closed
                            ),
                            contentDescription = null
                        )
                    }
                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF4F4F4),
                unfocusedContainerColor = Color(0xFFF4F4F4),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = AppColors.Primary
            )
        )
    }
}
