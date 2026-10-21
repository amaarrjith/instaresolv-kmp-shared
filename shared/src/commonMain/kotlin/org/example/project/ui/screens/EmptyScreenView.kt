package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_empty
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmptyScreenView(
    message: String,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    BoxWithConstraints(modifier = modifier) {
        if (maxHeight == Dp.Infinity) {
            // When measured with infinite height constraints (e.g. inside Column(Modifier.verticalScroll()) or LazyColumn items),
            // a scrollable container must not be used to avoid IllegalStateException.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                EmptyScreenContent(message = message)
            }
        } else {
            // In bounded containers (e.g. AppPullToRefreshBox on list screens), use LazyColumn
            // so pull-down-to-refresh gestures are properly intercepted and dispatched.
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                item {
                    EmptyScreenContent(message = message)
                }
            }
        }
    }
}

@Composable
private fun EmptyScreenContent(message: String) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
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
                size = 14.sp,
                weight = FontWeight.Normal,
                fontStyle = FontStyle.Italic
            ),
            color = AppColors.TextGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(top = 14.dp))
    }
}