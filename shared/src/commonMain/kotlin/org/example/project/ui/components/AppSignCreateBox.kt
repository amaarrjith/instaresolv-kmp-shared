package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_edit
import instaresolv.shared.generated.resources.ic_toast_close
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import org.example.project.ui.components.signaturepad.AppSignPicker
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppSignCreateBox(
    signatureUrl: String?,
    onSignatureUploaded: (String) -> Unit,
    onRemoveSignatureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showPicker = remember { mutableStateOf(false) }
    val showPreview = remember { mutableStateOf(false) }
    val isUploading = remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        AppSignPicker(
            showPicker = showPicker,
            onIsUploading = { isUploading.value = it },
            onSignatureUploaded = onSignatureUploaded
        )

        if (showPreview.value && !signatureUrl.isNullOrEmpty()) {
            AppImagePreviewDialog(
                imageUrl = signatureUrl,
                onDismiss = { showPreview.value = false }
            )
        }
        Text(
            text = buildAnnotatedString {
                append("Add Signature ")
                withStyle(SpanStyle(color = Color.Red)) { append("*") }
            },
            style = textStyle(12.sp, FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        val density = androidx.compose.ui.platform.LocalDensity.current
        val strokeWidthPx = with(density) { 1.dp.toPx() }
        val dashLengthPx = with(density) { 6.dp.toPx() }
        val gapLengthPx = with(density) { 6.dp.toPx() }

        val stroke = Stroke(
            width = strokeWidthPx,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLengthPx, gapLengthPx), 0f)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(16.dp)
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
                        .clickable(enabled = signatureUrl.isNullOrEmpty() && !isUploading.value) {
                            showPicker.value = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isUploading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AppColors.Primary,
                            strokeWidth = 2.dp
                        )
                    } else if (signatureUrl.isNullOrEmpty()) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_edit),
                            contentDescription = "Add Signature",
                            tint = Color(0xFF8F9098),
                            modifier = Modifier.size(40.dp)
                        )
                    } else {
                        WebImageView(
                            imageUrl = signatureUrl,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showPreview.value = true },
                            contentScale = ContentScale.Crop // Assuming WebImageView handles this
                        )
                    }
                }

                // Close Button
                if (!signatureUrl.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-8).dp)
                            .size(24.dp)
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .clickable { onRemoveSignatureClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_toast_close),
                            contentDescription = "Remove Signature",
                            tint = Color(0xFFFF4B4B),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

