package org.example.project.ui.screens

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_arrow_left
import kotlinx.coroutines.delay
import org.example.project.ui.components.ScormWebViewContainer
import org.example.project.utilites.LockScreenOrientation
import org.jetbrains.compose.resources.painterResource

@Composable
fun TrainingScormScreen(
    scormUrl: String,
    onBackClicked: () -> Unit
) {
    // Lock orientation to Landscape
    LockScreenOrientation(landscape = true)

    var showControls by remember { mutableStateOf(true) }
    var interactionTrigger by remember { mutableStateOf(0) }

    // Auto-hide back button after 2 seconds when not interacting
    LaunchedEffect(showControls, interactionTrigger) {
        if (showControls) {
            delay(2000)
            showControls = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Full-screen SCORM WebView — fills entire window including insets
        ScormWebViewContainer(
            url = scormUrl,
            modifier = Modifier.fillMaxSize()
        )

        // Floating back button — respects safe area insets so it's not hidden under status/nav bar
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier
                    .padding(8.dp)
                    .size(40.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_left),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
