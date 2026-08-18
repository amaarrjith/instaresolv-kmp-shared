package org.example.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.aiTranslated
import instaresolv.shared.generated.resources.noImagesFound
import instaresolv.shared.generated.resources.uploadedImages
import org.example.project.colors.AppColors
import org.example.project.data.model.UploadedImageData
import org.example.project.typography.textStyle
import org.example.project.ui.screens.EmptyScreenView
import org.jetbrains.compose.resources.stringResource

@Composable
fun UploadedImagesSection(
    images: List<UploadedImageData>?,
    onImageClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = stringResource(Res.string.uploadedImages),
    isTranslationDone: Boolean = false,
    showEmptyView: Boolean = true,
    emptyMessage: String = stringResource(Res.string.noImagesFound)
) {
    val validImages = images?.filter { !it.image.isNullOrBlank() || !it.description.isNullOrBlank() }

    if (!validImages.isNullOrEmpty()) {
        Column(modifier = modifier) {
            if (!title.isNullOrBlank()) {
                Text(
                    text = title,
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(12.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                validImages.forEach { img ->
                    Column {
                        if (!img.image.isNullOrEmpty()) {
                            WebImageView(
                                imageUrl = img.image,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onImageClick(img.image) },
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        if (!img.description.isNullOrEmpty()) {
                            Text(
                                text = img.description,
                                style = textStyle(size = 14.sp, weight = FontWeight.Normal),
                                color = AppColors.Black
                            )
                        }
                        if (isTranslationDone && !img.translatedImageDescription.isNullOrEmpty()) {
                            Text(
                                text = buildAnnotatedString {
                                    append(img.translatedImageDescription)
                                    withStyle(
                                        SpanStyle(
                                            color = AppColors.SkyBlue,
                                            fontWeight = FontWeight.Medium
                                        )
                                    ) {
                                        append(" (${stringResource(Res.string.aiTranslated)})")
                                    }
                                },
                                style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                                color = AppColors.SkyBlue
                            )
                        }
                    }
                }
            }
        }
    } else if (showEmptyView) {
        Column(modifier = modifier) {
            if (!title.isNullOrBlank()) {
                Text(
                    text = title,
                    style = textStyle(size = 12.sp, weight = FontWeight.Medium),
                    color = AppColors.TextGray
                )
                Spacer(Modifier.height(12.dp))
            }
            EmptyScreenView(message = emptyMessage)
        }
    }
}
