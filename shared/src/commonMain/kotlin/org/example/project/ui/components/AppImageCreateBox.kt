package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_gallery
import instaresolv.shared.generated.resources.ic_toast_close
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight

import org.example.project.ui.components.imagepicker.AppImagePicker
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import instaresolv.shared.generated.resources.ic_add_photo

@Composable
fun AppImageCreateBox(
    imageUrl: String?,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onImageUploaded: (String) -> Unit,
    onRemoveImageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showPicker = remember { mutableStateOf(false) }
    val showPreview = remember { mutableStateOf(false) }

    val isUploading = remember { mutableStateOf(false) }

    AppImagePicker(
        showPicker = showPicker,
        showFullScreenLoader = false,
        onIsUploading = { isUploading.value = it },
        onImageUploaded = onImageUploaded
    )

    val density = androidx.compose.ui.platform.LocalDensity.current
    val strokeWidthPx = with(density) { 1.dp.toPx() }
    val dashLengthPx = with(density) { 6.dp.toPx() }
    val gapLengthPx = with(density) { 6.dp.toPx() }

    val stroke = Stroke(
        width = strokeWidthPx,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLengthPx, gapLengthPx), 0f)
    )

    if (showPreview.value && !imageUrl.isNullOrEmpty()) {
        org.example.project.ui.components.AppImagePreviewDialog(
            imageUrl = imageUrl,
            onDismiss = { showPreview.value = false }
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                // Dashed border or Image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .clip(RoundedCornerShape(8))
                        .drawBehind {
                            drawRoundRect(
                                color = Color(0xFFDCDCDC),
                                style = stroke,
                                cornerRadius = CornerRadius(8.dp.toPx())
                            )
                        }
                        .clickable(enabled = imageUrl.isNullOrEmpty() && !isUploading.value) {
                            showPicker.value = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isUploading.value) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AppColors.Primary,
                            strokeWidth = 2.dp
                        )
                    } else if (imageUrl.isNullOrEmpty()) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_add_photo),
                            contentDescription = "Add Image",
                            tint = Color(0xFF8F9098),
                            modifier = Modifier.size(40.dp)
                        )
                    } else {
                        WebImageView(
                            imageUrl = imageUrl,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showPreview.value = true },
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Close Button
                if (!imageUrl.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-8).dp)
                            .size(24.dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .clickable { onRemoveImageClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_toast_close),
                            contentDescription = "Remove Image",
                            tint = Color(0xFFFF4B4B),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description TextField
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (description.isEmpty()) {
                    Text(
                        text = "Enter Description here",
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Medium
                        ),
                        color = Color(0xFF9E9E9E)
                    )
                }
                BasicTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Medium
                    ).copy(color = AppColors.Black),
                    cursorBrush = SolidColor(AppColors.Primary)
                )
            }
        }
    }
}
