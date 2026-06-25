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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_eye_open
import instaresolv.shared.generated.resources.ic_eye_closed
import instaresolv.shared.generated.resources.ic_search
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun AppTextField(
    icon: DrawableResource? = null,
    isMandatory: Boolean = false,
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    placeholder: String,
    isSecure: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    textFieldModifier: Modifier = Modifier.fillMaxWidth(),
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = title,
                style = textStyle(
                    size = 12.sp,
                    weight = FontWeight.SemiBold
                ),
                color = AppColors.Black
            )
            if (isMandatory) {
                Text(
                    text = "*",
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.SemiBold
                    ),
                    color = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.foundation.layout.Row(
            modifier = textFieldModifier
                .height(44.dp)
                .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Medium
                        ),
                        color = Color(0xFF9E9E9E)
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = singleLine,
                    textStyle = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Medium
                    ).copy(color = AppColors.Black),
                    enabled = enabled,
                    readOnly = readOnly,
                    visualTransformation = if (isSecure && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(AppColors.Primary)
                )
            }
            
            if (isSecure) {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.padding(start = 8.dp).size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (passwordVisible) Res.drawable.ic_eye_open else Res.drawable.ic_eye_closed
                        ),
                        contentDescription = null
                    )
                }
            } else {
                icon?.let {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.padding(start = 8.dp).size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AppSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_search),
            contentDescription = null,
            modifier = Modifier.size(24.dp).padding(end = 8.dp),
            tint = Color(0xFF9E9E9E)
        )
        
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Medium
                    ),
                    color = Color(0xFF9E9E9E)
                )
            }
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = textStyle(
                    size = 14.sp,
                    weight = FontWeight.Medium
                ).copy(color = AppColors.Black),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(AppColors.Primary)
            )
        }
    }
}
