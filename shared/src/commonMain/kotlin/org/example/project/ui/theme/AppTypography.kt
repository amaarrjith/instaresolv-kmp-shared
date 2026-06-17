package org.example.project.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import org.example.project.colors.AppColors
import org.jetbrains.compose.resources.Font
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.inter_regular
import instaresolv.shared.generated.resources.inter_medium
import instaresolv.shared.generated.resources.inter_semibold
import instaresolv.shared.generated.resources.inter_bold

@Composable
fun interFontFamily(): FontFamily = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold)
)

@Composable
fun textStyle(
    size: TextUnit = 14.sp,
    weight: FontWeight = FontWeight.Normal,
    lineHeight: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    color: Color = AppColors.Black
): TextStyle {
    return TextStyle(
        fontFamily = interFontFamily(),
        fontWeight = weight,
        fontSize = size,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing,
        color = color
    )
}
