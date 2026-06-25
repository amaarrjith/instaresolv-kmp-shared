package org.example.project.ui.components
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_placeholder_project
import org.jetbrains.compose.resources.painterResource

@Composable
fun WebImageView(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillBounds
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
        error = painterResource(Res.drawable.ic_placeholder_project),
        placeholder = painterResource(Res.drawable.ic_placeholder_project),
        onError = {
            println("`ic_placeholder_project` load error: ${it.result.throwable}")
        }
    )
}