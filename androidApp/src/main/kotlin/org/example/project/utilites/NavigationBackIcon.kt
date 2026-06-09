package org.example.project.utilites

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.example.project.R

@Composable
fun NavigationBackIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(R.drawable.ic_arrow_left),
        contentDescription = null,
        modifier = modifier
            .clickable { onClick() }
            .padding(20.dp)
    )
}
