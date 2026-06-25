package org.example.project.utilites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle

@Composable
fun AppPrimaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    fillMaxWidth: Boolean = true
) {
    if (isLoading) {
        Loader()
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = modifier
                .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)
                .heightIn(min = 50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary
            )
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = textStyle(
                    size = 16.sp,
                    weight = FontWeight.SemiBold
                ),
                color = Color.White
            )
        }
    }
}


@Composable
fun Loader() {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(AppColors.Primary),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White
        )
    }
}
