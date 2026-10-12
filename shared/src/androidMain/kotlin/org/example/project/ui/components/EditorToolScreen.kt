package org.example.project.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun EditorToolScreen(
    imageBytes: ByteArray,
    onClose: () -> Unit,
    onSave: (Uri) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            factory = { context ->
                EditorToolView(context).apply {
                    setImageBytes(
                        imageBytes = imageBytes,
                        destinationFileName = "edited_image_${System.currentTimeMillis()}.jpg",
                        callback = object : EditorToolListener {
                            override fun onClose() {
                                onClose()
                            }

                            override fun onSaveClick(uri: Uri) {
                                onSave(uri)
                            }
                        }
                    )
                }
            }
        )
    }
}
