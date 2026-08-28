package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_empty
import instaresolv.shared.generated.resources.ic_empty_icon
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmptyScreenView(
    message: String,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Column(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(Res.drawable.ic_empty),
            contentDescription = "Empty",
            modifier = Modifier.wrapContentSize(),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.padding(top = 14.dp))

        Text(
            text = message,
            style = textStyle(
                size = 16.sp,
                weight = FontWeight.Medium
            ),
            color = AppColors.TextGray
        )

        Spacer(modifier = Modifier.padding(top = 14.dp))
    }
}