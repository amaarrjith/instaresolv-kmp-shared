package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_mic_red
import instaresolv.shared.generated.resources.ic_voice_mic
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.utilites.AppPrimaryButton
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.resources.painterResource
import org.example.project.utilites.rememberAudioRecorder
import org.example.project.utilites.rememberAudioPlayer
import androidx.compose.foundation.layout.width
import instaresolv.shared.generated.resources.ic_audioplayer_play
import instaresolv.shared.generated.resources.ic_audioplayer_speaker
import instaresolv.shared.generated.resources.ic_play
import instaresolv.shared.generated.resources.ic_trash
import instaresolv.shared.generated.resources.ic_trash_player
import org.example.project.utilites.AppBorderButton
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.draw.scale
import org.koin.compose.koinInject
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import org.example.project.utilites.readAudioFileBytes
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMultilineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    title: String = "",
    placeholder: String,
    isVoiceEnabled: Boolean = false,
    onAudioUrlProcessed: (String?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showVoicePopup by remember { mutableStateOf(false) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }

    if (showVoicePopup) {
        ModalBottomSheet(
            onDismissRequest = { showVoicePopup = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
        ) {
            RecordAudioContentView(
                onDismiss = { showVoicePopup = false },
                onDone = { path ->
                    onAudioUrlProcessed(path)
                    audioFilePath = path
                    showVoicePopup = false
                }
            )
        }
    }

    Column(modifier = modifier) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = textStyle(size = 12.sp, weight = FontWeight.SemiBold),
                color = AppColors.Black
            )
            Spacer(Modifier.height(8.dp))
        }
        Box {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        text = placeholder,
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Normal
                        ),
                        color = AppColors.TextGray
                    )
                },
                textStyle = textStyle(
                    size = 14.sp,
                    weight = FontWeight.Normal
                ).copy(
                    color = AppColors.Black
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Primary,
                    unfocusedBorderColor = Color(0xFFE5E5EA)
                )
            )

            if (isVoiceEnabled && audioFilePath == null) {
                Image(
                    painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_voice_mic),
                    contentDescription = stringResource(Res.string.voiceIcon),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .size(24.dp)
                        .clickable {
                            showVoicePopup = true
                        }
                )
            }
        }
        if (audioFilePath != null) {
            Spacer(Modifier.height(16.dp))
            AppAudioPlayer(
                audioFilePath!!,
                onDeleteAudio = {
                    onAudioUrlProcessed(null)
                    audioFilePath = null
                },
                isDelete = true
            )
        }
    }
}

@Composable
fun AppAudioPlayer(
    filePath: String,
    isDelete: Boolean = false,
    isTranslationRequired: Boolean = false,
    onTranslateButtonClick: () -> Unit = {},
    onDeleteAudio: () -> Unit = {}
) {
    val audioPlayer = rememberAudioPlayer()
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var isUserDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(0L) }

    LaunchedEffect(filePath) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            audioPlayer.prepare(filePath)
        }
        var attempts = 0
        while (duration <= 0L && attempts < 50) {
            duration = audioPlayer.getDuration()
            if (duration <= 0L) {
                kotlinx.coroutines.delay(200)
            }
            attempts++
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                if (duration == 0L) {
                    duration = audioPlayer.getDuration()
                }
                if (!isUserDragging) {
                    currentPosition = audioPlayer.getCurrentPosition()
                }
                if (!audioPlayer.isPlaying()) {
                    isPlaying = false
                }
                kotlinx.coroutines.delay(100)
            }
        } else {
            // Update current position even when paused to reflect seek changes immediately
            if (!isUserDragging) {
                currentPosition = audioPlayer.getCurrentPosition()
            }
        }
    }

    val formatTime = { timeMs: Long ->
        val totalSeconds = timeMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isTranslationRequired) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .background(AppColors.Primary, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onTranslateButtonClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.translate),
                        style = textStyle(size = 10.sp, weight = FontWeight.Medium),
                        color = Color.White
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0XFFFEF3EC), shape = RoundedCornerShape(30.dp))
                .padding(horizontal = 12.dp)
                .padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AppColors.Primary, shape = CircleShape)
                    .clickable {
                        if (isPlaying) {
                            audioPlayer.pause()
                            isPlaying = false
                        } else {
                            audioPlayer.play(filePath)
                            isPlaying = true
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    Row(
                        modifier = Modifier.size(14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0.9f)
                                .width(3.dp)
                                .background(Color.White, RoundedCornerShape(1.dp))
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(0.9f)
                                .width(3.dp)
                                .background(Color.White, RoundedCornerShape(1.dp))
                        )
                    }
                } else {
                    Canvas(modifier = Modifier.size(16.dp)) {
                        val path = Path().apply {
                            val w = size.width
                            val h = size.height
                            moveTo(w * 0.2f, h * 0.1f)
                            lineTo(w * 0.8f, h * 0.5f)
                            lineTo(w * 0.2f, h * 0.9f)
                            close()
                        }
                        drawPath(path, color = Color.White)
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = formatTime(if (isUserDragging) dragPosition else currentPosition),
                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                color = AppColors.TextGray,
                modifier = Modifier.width(36.dp)
            )

            androidx.compose.material3.Slider(
                value = if (duration > 0) {
                    if (isUserDragging) dragPosition.toFloat() / duration.toFloat()
                    else currentPosition.toFloat() / duration.toFloat()
                } else 0f,
                onValueChange = { value ->
                    isUserDragging = true
                    dragPosition = (value * duration).toLong()
                },
                onValueChangeFinished = {
                    audioPlayer.seekTo(dragPosition)
                    currentPosition = dragPosition
                    isUserDragging = false
                },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = AppColors.Primary,
                    activeTrackColor = AppColors.Primary,
                    inactiveTrackColor = Color.LightGray
                )
            )

            Text(
                text = formatTime(duration),
                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                color = AppColors.TextGray,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.End
            )

            Spacer(Modifier.width(8.dp))

            if (isDelete) {
                Image(
                    painter = painterResource(Res.drawable.ic_trash_player),
                    contentDescription = stringResource(Res.string.delete),
                    modifier = Modifier.size(32.dp)
                        .clickable {
                            audioPlayer.stop()
                            onDeleteAudio()
                        }
                )
            }
        }
    }
}

@Composable
fun RecordAudioContentView(onDismiss: () -> Unit, onDone: (String) -> Unit) {
    var isRecording by remember { mutableStateOf(false) }
    var hasRecorded by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    val audioRecorder = rememberAudioRecorder()
    val audioRepository = koinInject<org.example.project.domain.repository.AudioRepository>()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 50.dp)
            .padding(vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isRecording) {
                val infiniteTransition = rememberInfiniteTransition()
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.5f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = LinearOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = LinearOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(scale)
                        .background(AppColors.Primary.copy(alpha = alpha), shape = CircleShape)
                )
            }
            Image(
                painter = painterResource(Res.drawable.ic_mic_red),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }
        Spacer(modifier = Modifier.height(38.dp))
        var recordingDuration by remember { mutableStateOf(0L) }

        LaunchedEffect(isRecording) {
            if (isRecording) {
                while (true) {
                    kotlinx.coroutines.delay(1000L)
                    recordingDuration++
                }
            } else {
                if (!hasRecorded) {
                    recordingDuration = 0L
                }
            }
        }

        val hours = recordingDuration / 3600
        val minutes = (recordingDuration % 3600) / 60
        val seconds = recordingDuration % 60
        val timeText = "${hours.toString().padStart(2, '0')} : ${minutes.toString().padStart(2, '0')} : ${seconds.toString().padStart(2, '0')}"

        Text(
            text = timeText,
            style = textStyle(size = 18.sp, weight = FontWeight.SemiBold),
            color = AppColors.TextGray
        )
        Spacer(modifier = Modifier.height(30.dp))
        
        AudioSpectrumVisualizer(isRecording = isRecording)

        Spacer(modifier = Modifier.height(50.dp))
        
        if (!hasRecorded) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            LaunchedEffect(isPressed) {
                if (isPressed) {
                    isRecording = true
                    audioRecorder.startRecording()
                } else if (isRecording) {
                    isRecording = false
                    audioRecorder.stopRecording()
                    hasRecorded = true
                }
            }

            Button(
                onClick = {},
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                )
            ) {
                Text(
                    text = if (isRecording) "Recording..." else "Hold To Record",
                    textAlign = TextAlign.Center,
                    style = textStyle(
                        size = 16.sp,
                        weight = FontWeight.SemiBold
                    ),
                    color = Color.White
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AppBorderButton(
                    title = stringResource(Res.string.discard),
                    onClick = { 
                        hasRecorded = false
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                )
                if (isUploading) {
                    Box(modifier = Modifier.weight(1f).padding(start = 8.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                            color = AppColors.Primary
                        )
                    }
                } else {
                    AppPrimaryButton(
                        title = stringResource(Res.string.done),
                        onClick = { 
                            val path = audioRecorder.getRecordFilePath()
                            if (path != null) {
                                isUploading = true
                                scope.launch {
                                    try {
                                        val bytes = readAudioFileBytes(path)
                                        val response = audioRepository.uploadAudio(bytes)
                                        isUploading = false
                                        if (response is org.example.project.network.NetworkResult.Success) {
                                            val audioUrl = response.data.audioUrl
                                            if (audioUrl != null) {
                                                onDone(audioUrl)
                                            } else {
                                                onDismiss()
                                            }
                                        } else {
                                            onDismiss()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        isUploading = false
                                        onDismiss()
                                    }
                                }
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AudioSpectrumVisualizer(isRecording: Boolean) {
    val barCount = 20
    val infiniteTransition = rememberInfiniteTransition()
    
    Row(
        modifier = Modifier.fillMaxWidth().height(30.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val randomDuration = remember { (300..800).random() }
            val height by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = randomDuration, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            
            val barHeight = if (isRecording) height else 0.1f

            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .width(4.dp)
                    .fillMaxHeight(barHeight)
                    .background(color = AppColors.Primary, shape = RoundedCornerShape(2.dp))
            )
        }
    }
}
