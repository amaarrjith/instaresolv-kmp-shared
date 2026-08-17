package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_exit
import org.example.project.App
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppExitPopup(
    visible: Boolean,
    title: String = "Leave this page?",
    description: String = "Are you sure you want to leave this page? Unsaved changes will be lost.",
    icon: DrawableResource = Res.drawable.ic_exit,
    primaryButtonText: String = "Yes, Leave",
    primaryButtonColor: Color = AppColors.Primary,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String = "No, Stay",
    secondaryButtonColor: Color = AppColors.SkyBlue,
    onSecondaryClick: () -> Unit,
    onDismiss: () -> Unit
) {
    if (visible) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (icon != null) {
                        Image(
                            painter = painterResource(icon),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Text(
                        text = title,
                        style = textStyle(size = 20.sp, weight = FontWeight.Bold),
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    if (description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = description,
                            style = textStyle(size = 14.sp, weight = FontWeight.Normal),
                            color = Color(0xFF6B7280),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        androidx.compose.material3.Button(
                            onClick = onSecondaryClick,
                            modifier = Modifier.weight(1f).heightIn(min = 50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = secondaryButtonColor
                            )
                        ) {
                            Text(
                                text = secondaryButtonText,
                                style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                                color = Color.White
                            )
                        }

                        androidx.compose.material3.Button(
                            onClick = onPrimaryClick,
                            modifier = Modifier.weight(1f).heightIn(min = 50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = primaryButtonColor
                            )
                        ) {
                            Text(
                                text = primaryButtonText,
                                style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}