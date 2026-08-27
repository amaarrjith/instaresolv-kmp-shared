package org.example.project.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CustomVideoPlayer(
    url: String,
    lastPlaybackTimeSeconds: Int,
    isPlaying: Boolean,
    onProgressUpdate: (currentSeconds: Long, totalSeconds: Long) -> Unit,
    onIsLoadingChange: (Boolean) -> Unit,
    onError: (String) -> Unit,
    seekToSeconds: Long?,
    onSeekCompleted: () -> Unit,
    cookies: Map<String, String> = emptyMap(),
    modifier: Modifier = Modifier
)
