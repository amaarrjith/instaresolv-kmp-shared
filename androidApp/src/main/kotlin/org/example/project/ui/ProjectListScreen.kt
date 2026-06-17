package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.http.cio.Request
import org.example.project.R
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.utilites.NavigationBackIcon

@Composable
fun ProjectListScreen() {
    Scaffold(
        containerColor = Color.White,
        topBar = {
            ProjectListScreenTopBar()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {

        }
    }
}

@Composable
fun ProjectListScreenTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationBackIcon(
            onClick = {}
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = "PROJECTS",
            style = textStyle(
                size = 14.sp,
                weight = FontWeight.Bold
            ),
            color = AppColors.Black
        )
        Spacer(modifier = Modifier.weight(1f))
        RequestButton()
        CreateButton()
    }
}

@Composable
fun RequestButton() {
    Box(
        modifier = Modifier

            .padding(
                10.dp
            )
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.SkyBlueLight)
                .padding(horizontal = 7.dp)
                .padding(vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = null
            )

            Text(
                text = "Request",
                style = textStyle(
                    size = 13.sp,
                    weight = FontWeight.Bold
                ),
                color = AppColors.SkyBlue
            )
        }
    }
}

@Composable
fun CreateButton() {
    Box(
        modifier = Modifier

            .padding(
                10.dp
            )
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.SkyBlueLight)
                .padding(horizontal = 7.dp)
                .padding(vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null
            )

            Text(
                text = "Create",
                style = textStyle(
                    size = 13.sp,
                    weight = FontWeight.Bold
                ),
                color = AppColors.Primary
            )
        }
    }
}

@Preview
@Composable
fun ProjectListScreenPreview() {
    ProjectListScreen()
}