package org.example.project.utilites

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AppWebView(url: String, modifier: Modifier = Modifier)
