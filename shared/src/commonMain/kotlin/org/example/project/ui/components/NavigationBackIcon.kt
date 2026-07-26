package org.example.project.utilites

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_arrow_left
import org.example.project.ui.viewmodel.GlobalSettingsViewModel
import org.koin.compose.koinInject

@Composable
fun NavigationBackIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val globalSettingsViewModel: GlobalSettingsViewModel = koinInject()
    val currentLanguage by globalSettingsViewModel.currentLanguage.collectAsState()
    Image(
        painter = painterResource(Res.drawable.ic_arrow_left),
        contentDescription = null,
        modifier = modifier
            .clickable { onClick() }
            .padding(20.dp)
            .scale(scaleX = if (currentLanguage.isRtl) -1f else 1f, scaleY = 1f)
    )
}
