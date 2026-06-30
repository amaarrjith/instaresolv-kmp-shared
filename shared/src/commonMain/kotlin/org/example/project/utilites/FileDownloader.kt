package org.example.project.utilites

import androidx.compose.runtime.Composable

expect class FileDownloader {
    fun downloadFile(url: String, fileName: String)
}

@Composable
expect fun rememberFileDownloader(): FileDownloader
