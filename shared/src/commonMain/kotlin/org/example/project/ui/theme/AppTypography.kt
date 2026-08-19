package org.example.project.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
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
import instaresolv.shared.generated.resources.poppins_bold
import instaresolv.shared.generated.resources.poppins_medium
import instaresolv.shared.generated.resources.poppins_regular
import instaresolv.shared.generated.resources.poppins_italic
import instaresolv.shared.generated.resources.poppins_semibold
import instaresolv.shared.generated.resources.noto_naskh_arabic_regular
import instaresolv.shared.generated.resources.noto_naskh_arabic_medium
import instaresolv.shared.generated.resources.noto_naskh_arabic_semibold
import instaresolv.shared.generated.resources.noto_naskh_arabic_bold
import instaresolv.shared.generated.resources.plus_jakarta_sans_bold
import instaresolv.shared.generated.resources.plus_jakarta_sans_italic
import instaresolv.shared.generated.resources.plus_jakarta_sans_medium
import instaresolv.shared.generated.resources.plus_jakarta_sans_regular
import instaresolv.shared.generated.resources.plus_jakarta_sans_semibold
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@Composable
fun interFontFamily(): FontFamily = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold)
)

@Composable
fun notoNaskhFontFamily(): FontFamily = FontFamily(
    Font(Res.font.noto_naskh_arabic_regular, FontWeight.Normal),
    Font(Res.font.noto_naskh_arabic_medium, FontWeight.Medium),
    Font(Res.font.noto_naskh_arabic_semibold, FontWeight.SemiBold),
    Font(Res.font.noto_naskh_arabic_bold, FontWeight.Bold)
)

@Composable
fun poppinsFontFamily(): FontFamily = FontFamily(
    Font(Res.font.poppins_regular, FontWeight.Normal),
    Font(Res.font.poppins_italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.poppins_medium, FontWeight.Medium),
    Font(Res.font.poppins_semibold, FontWeight.SemiBold),
    Font(Res.font.poppins_bold, FontWeight.Bold)
)

@Composable
fun plusJakartaSansFontFamily(): FontFamily = FontFamily(
    Font(Res.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(Res.font.plus_jakarta_sans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(Res.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(Res.font.plus_jakarta_sans_bold, FontWeight.Bold)
)

@Composable
fun textStyle(
    size: TextUnit = 14.sp,
    weight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    lineHeight: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    color: Color = AppColors.Black
): TextStyle {
    return TextStyle(
        fontFamily = plusJakartaSansFontFamily(),
        fontWeight = weight,
        fontStyle = fontStyle,
        fontSize = size,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing,
        color = color
    )
}
