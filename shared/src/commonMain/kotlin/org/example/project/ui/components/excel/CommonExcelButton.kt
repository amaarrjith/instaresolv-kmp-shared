package org.example.project.ui.components.excel

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_download
import org.example.project.colors.AppColors
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun CommonExcelButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Box {
        FloatingActionButton(
            onClick = {
                if (!isLoading) {
                    onClick()
                }
            },
            shape = CircleShape,
            containerColor = AppColors.Primary,
            contentColor = Color.White
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Image(
                    painter = painterResource(Res.drawable.ic_download),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
        }
    }
}
