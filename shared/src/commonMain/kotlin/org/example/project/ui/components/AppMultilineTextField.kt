package org.example.project.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle

@Composable
fun AppMultilineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    title: String = "",
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                color = AppColors.Black
            )
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Normal
                    ),
                    color = AppColors.TextGray
                )
            },
            textStyle = textStyle(
                size = 14.sp,
                weight = FontWeight.Normal
            ).copy(
                color = AppColors.Black
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.Primary,
                unfocusedBorderColor = Color(0xFFE5E5EA)
            )
        )
    }
}
