package org.example.project.utilites

import androidx.compose.runtime.Composable

expect class TextSharer {
    fun shareText(text: String, title: String? = null)
}

@Composable
expect fun rememberTextSharer(): TextSharer
