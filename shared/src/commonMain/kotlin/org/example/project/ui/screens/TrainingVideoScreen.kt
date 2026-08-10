package org.example.project.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_arrow_left
import instaresolv.shared.generated.resources.ic_play
import org.example.project.utilites.rtlScale
import kotlinx.coroutines.delay
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.ui.components.CustomVideoPlayer
import org.example.project.ui.components.AppLoader
import org.example.project.utilites.ErrorRetryView
import org.example.project.utilites.LockScreenOrientation
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@Composable
fun TrainingVideoScreen(
    trainingId: Int,
    onBackClicked: () -> Unit
) {
    // Lock orientation to Landscape
    LockScreenOrientation(landscape = true)

    val viewModel: TrainingVideoViewModel = koinInject(
        parameters = { parametersOf(trainingId) }
    )
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            AppLoader()
        } else if (uiState.error != null) {
            ErrorRetryView(
                errorMessage = uiState.error ?: "",
                onRetryClick = { viewModel.loadTrainingVideoUrl() }
            )
        } else {
            uiState.videoData?.let { videoData ->
                VideoPlayerContainer(
                    videoUrl = videoData.videoUrl,
                    initialPlaybackTimeSeconds = videoData.lastPlayBackTime ?: 0,
                    trainingTitle = uiState.trainingTitle,
                    onBackClicked = onBackClicked,
                    onUpdateProgress = { viewModel.updateVideoProgress(it) }
                )
            }
        }
    }
}

@Composable
fun VideoPlayerContainer(
    videoUrl: String,
    initialPlaybackTimeSeconds: Int,
    trainingTitle: String,
    onBackClicked: () -> Unit,
    onUpdateProgress: (Long) -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var currentProgressSeconds by remember { mutableStateOf(0L) }
    var totalDurationSeconds by remember { mutableStateOf(0L) }
    var isVideoLoading by remember { mutableStateOf(true) }
    var videoError by remember { mutableStateOf<String?>(null) }
    var seekToSeconds by remember { mutableStateOf<Long?>(null) }
    var lastSyncedSeconds by remember { mutableStateOf(0L) }
    var showControls by remember { mutableStateOf(true) }
    var interactionTrigger by remember { mutableStateOf(0) }

    // Auto-hide controls timer resets on any user interaction trigger
    LaunchedEffect(showControls, isPlaying, interactionTrigger) {
        if (showControls && isPlaying) {
            delay(3000)
            showControls = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
                interactionTrigger++
            }
    ) {
        // Underlay: Native Video Player view
        CustomVideoPlayer(
            url = videoUrl,
            lastPlaybackTimeSeconds = initialPlaybackTimeSeconds,
            isPlaying = isPlaying,
            onProgressUpdate = { current, total ->
                // Only update progress if we are not actively seeking
                if (seekToSeconds == null) {
                    currentProgressSeconds = current
                    totalDurationSeconds = total
                    
                    // Sync progress every 5 seconds
                    if (current > 0 && current % 5 == 0L && current != lastSyncedSeconds) {
                        lastSyncedSeconds = current
                        onUpdateProgress(current)
                    }
                }
            },
            onIsLoadingChange = { isVideoLoading = it },
            onError = { videoError = it },
            seekToSeconds = seekToSeconds,
            onSeekCompleted = {
                seekToSeconds = null
            },
            modifier = Modifier.fillMaxSize()
        )

        // Transparent overlay to catch clicks on the video when controls are hidden
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    showControls = !showControls
                    interactionTrigger++
                }
        )

        // Loading overlay
        if (isVideoLoading && videoError == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                AppLoader()
            }
        }

        // Error overlay
        if (videoError != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = videoError ?: "Playback Error",
                    style = textStyle(size = 14.sp, weight = FontWeight.Bold, color = Color.Red),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Overlay: Custom Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                // Top controls bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { interactionTrigger++ } // Consume clicks so they don't toggle controls
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            onUpdateProgress(currentProgressSeconds)
                            onBackClicked()
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_left),
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp).rtlScale()
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = trainingTitle,
                        style = textStyle(size = 16.sp, weight = FontWeight.Bold, color = Color.White)
                    )
                }

                // Center controls overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { interactionTrigger++ } // Consume clicks
                ) {
                    IconButton(
                        onClick = {
                            isPlaying = !isPlaying
                            interactionTrigger++
                        },
                        modifier = Modifier.size(64.dp),
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f))
                    ) {
                        if (isPlaying) {
                            // Custom Pause icon
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(width = 6.dp, height = 24.dp).background(Color.White))
                                Box(modifier = Modifier.size(width = 6.dp, height = 24.dp).background(Color.White))
                            }
                        } else {
                            // Play icon
                            Icon(
                                painter = painterResource(Res.drawable.ic_play),
                                contentDescription = stringResource(Res.string.play),
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // Bottom controls bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { interactionTrigger++ } // Consume clicks
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Play/Pause Action in Bottom
                        IconButton(
                            onClick = {
                                isPlaying = !isPlaying
                                interactionTrigger++
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            if (isPlaying) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(width = 4.dp, height = 14.dp).background(Color.White))
                                    Box(modifier = Modifier.size(width = 4.dp, height = 14.dp).background(Color.White))
                                }
                            } else {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_play),
                                    contentDescription = stringResource(Res.string.play),
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Time Labels: Format MM:SS / MM:SS
                        Text(
                            text = "${formatTime(currentProgressSeconds)} / ${formatTime(totalDurationSeconds)}",
                            style = textStyle(size = 12.sp, weight = FontWeight.Medium, color = Color.White)
                        )
                    }

                    // Scrubber (Slider) seek bar
                    val sliderValue = if (totalDurationSeconds > 0) {
                        currentProgressSeconds.toFloat() / totalDurationSeconds.toFloat()
                    } else 0f

                    Slider(
                        value = sliderValue,
                        onValueChange = { /* Disabled seeking */ },
                        onValueChangeFinished = { /* Disabled seeking */ },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFD42027),
                            activeTrackColor = Color(0xFFD42027),
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth().height(16.dp)
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Long): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
}
