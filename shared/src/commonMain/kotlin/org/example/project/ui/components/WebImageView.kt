package org.example.project.ui.components
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_placeholder_project
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun WebImageView(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    placeHolder: DrawableResource = Res.drawable.ic_placeholder_project,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.FillBounds,
    showPlaceholderBorder: Boolean = false
) {
    val modelUrl = imageUrl?.takeIf { it.isNotBlank() }

    AsyncImage(
        model = modelUrl,
        contentDescription = contentDescription,
        modifier = modifier.then(
            if (showPlaceholderBorder && modelUrl == null) {
                Modifier.border(
                    width = 1.dp,
                    color = Color(0xFFD9D9D9),
                    shape = CircleShape
                )
            } else {
                Modifier
            }
        ),
        contentScale = contentScale,
        error = painterResource(placeHolder),
        placeholder = painterResource(placeHolder)
    )
}