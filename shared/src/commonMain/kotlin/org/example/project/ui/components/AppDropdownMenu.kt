package org.example.project.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.window.PopupProperties
import androidx.compose.foundation.BorderStroke

@Composable
fun AppDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: PopupProperties = PopupProperties(focusable = true),
    content: @Composable ColumnScope.() -> Unit
) {
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(8.dp)),
        colorScheme = MaterialTheme.colorScheme.copy(surface = Color(0xFFF4F0F7))
    ) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            properties = properties,
            shape = RoundedCornerShape(8.dp),
            containerColor = Color(0xFFF4F0F7),
            border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
            content = content
        )
    }
}
