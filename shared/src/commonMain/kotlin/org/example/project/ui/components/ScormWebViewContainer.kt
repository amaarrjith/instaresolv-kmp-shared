package org.example.project.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ScormWebViewContainer(
    url: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
)
