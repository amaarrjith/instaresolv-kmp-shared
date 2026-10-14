package org.example.project.utilites

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

actual class TextSharer(private val context: Context) {
    actual fun shareText(text: String, title: String?) {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                if (!title.isNullOrBlank()) {
                    putExtra(Intent.EXTRA_SUBJECT, title)
                    putExtra(Intent.EXTRA_TITLE, title)
                }
                type = "text/plain"
            }
            val chooserIntent = Intent.createChooser(sendIntent, title ?: "Share")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
actual fun rememberTextSharer(): TextSharer {
    val context = LocalContext.current
    return remember { TextSharer(context) }
}
