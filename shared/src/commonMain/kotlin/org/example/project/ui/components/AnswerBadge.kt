package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.*
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AnswerBadge(
    selectedAnswer: Int?,
    modifier: Modifier = Modifier,
    iconSize: Dp = 16.dp,
    naText: String = "N/A",
    style: TextStyle = textStyle(12.sp, FontWeight.SemiBold),
    color: Color = AppColors.Black
) {
    val resolvedNaText = if (naText == "N/A") stringResource(Res.string.na) else naText
    when (selectedAnswer) {
        1 -> {
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(Res.drawable.ic_yes),
                    contentDescription = stringResource(Res.string.yes),
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(Res.string.yes), style = style, color = color)
            }
        }
        2 -> {
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(Res.drawable.ic_no),
                    contentDescription = stringResource(Res.string.no),
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(Res.string.no), style = style, color = color)
            }
        }
        3 -> {
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(Res.drawable.ic_na),
                    contentDescription = resolvedNaText,
                    modifier = Modifier.size(iconSize)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = resolvedNaText, style = style, color = color)
            }
        }
        else -> {
            Text(text = "-", style = style, color = AppColors.TextGray, modifier = modifier)
        }
    }
}

@Composable
fun AnswerBadge(
    answer: String?,
    modifier: Modifier = Modifier,
    iconSize: Dp = 16.dp,
    naText: String = "N/A",
    style: TextStyle = textStyle(12.sp, FontWeight.SemiBold),
    color: Color = AppColors.Black
) {
    when (answer?.trim()?.lowercase()) {
        "yes", "1" -> AnswerBadge(1, modifier, iconSize, naText, style, color)
        "no", "2" -> AnswerBadge(2, modifier, iconSize, naText, style, color)
        "na", "n/a", "not applicable", "3" -> AnswerBadge(3, modifier, iconSize, naText, style, color)
        null, "", "-" -> Text(text = "-", style = style, color = AppColors.TextGray, modifier = modifier)
        else -> Text(text = answer, style = style, color = color, modifier = modifier)
    }
}

@Composable
fun AnswerBadge(
    isYes: Boolean,
    modifier: Modifier = Modifier,
    iconSize: Dp = 16.dp,
    style: TextStyle = textStyle(12.sp, FontWeight.SemiBold),
    color: Color = AppColors.Black
) {
    AnswerBadge(
        selectedAnswer = if (isYes) 1 else 2,
        modifier = modifier,
        iconSize = iconSize,
        style = style,
        color = color
    )
}
