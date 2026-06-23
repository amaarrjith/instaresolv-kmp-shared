package org.example.project.utilites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_error_icon
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.painterResource

@Composable
fun ErrorRetryView(
    errorMessage: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(Res.drawable.ic_error_icon),
            contentDescription = "Error"
        )

        Spacer(modifier = Modifier.padding(top = 14.dp))

        Text(
            text = errorMessage,
            style = textStyle(
                size = 16.sp,
                weight = FontWeight.Medium
            ),
            color = AppColors.Primary
        )

        Spacer(modifier = Modifier.padding(top = 14.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.dp,
                    color = AppColors.Primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onRetryClick() }
                .padding(
                    horizontal = 20.dp,
                    vertical = 10.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Try Again",
                style = textStyle(
                    size = 14.sp,
                    weight = FontWeight.SemiBold
                ),
                color = AppColors.Primary
            )
        }
    }
}

@Composable
@Preview
fun ErrorRetryViewPreview() {
    ErrorRetryView(
        errorMessage = "Something went wrong",
        onRetryClick = {}
    )
}