package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import instaresolv.shared.generated.resources.ic_sucess_logo
import instaresolv.shared.generated.resources.ic_toast_alert
import org.example.project.typography.textStyle
import org.example.project.utilites.AppPrimaryButton
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppStatusDialog(
    visible: Boolean,
    title: String,
    description: String,
    buttonText: String = "Close",
    icon: DrawableResource = Res.drawable.ic_sucess_logo,
    onDismiss: () -> Unit
) {
    if (visible) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Column(
                    modifier = Modifier.padding(top = 40.dp, bottom = 24.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(24.dp))
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
                            color = Color(0xFF6B7280), // Gray text
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    AppPrimaryButton(
                        title = buttonText,
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun AppSuccessDialog(
    visible: Boolean,
    title: String,
    description: String,
    buttonText: String = "Close",
    onDismiss: () -> Unit
) {
    AppStatusDialog(
        visible = visible,
        title = title,
        description = description,
        buttonText = buttonText,
        icon = Res.drawable.ic_sucess_logo,
        onDismiss = onDismiss
    )
}

@Composable
fun AppErrorDialog(
    visible: Boolean,
    title: String,
    description: String,
    buttonText: String = "Close",
    onDismiss: () -> Unit
) {
    AppStatusDialog(
        visible = visible,
        title = title,
        description = description,
        buttonText = buttonText,
        icon = Res.drawable.ic_toast_alert, // Placeholder for error icon
        onDismiss = onDismiss
    )
}

@Composable
fun AppConfirmationDialog(
    visible: Boolean,
    title: String,
    description: String,
    icon: DrawableResource? = null,
    iconTint: Color? = null,
    primaryButtonText: String,
    primaryButtonColor: Color,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String,
    secondaryButtonColor: Color,
    onSecondaryClick: () -> Unit,
    onDismiss: () -> Unit
) {
    if (visible) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (icon != null) {
                        Image(
                            painter = painterResource(icon),
                            contentDescription = null,
                            colorFilter = iconTint?.let { androidx.compose.ui.graphics.ColorFilter.tint(it) }
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

@Composable
fun AppExitDialog(
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AppConfirmationDialog(
        visible = visible,
        title = "Leave this page?",
        description = "Are you sure you want to leave this page?\nUnsaved changes will be lost.",
        icon = Res.drawable.ic_toast_alert, // NOTE: Replace with Res.drawable.ic_exit_popup if available
        iconTint = null,
        primaryButtonText = "Yes, Leave",
        primaryButtonColor = Color(0xFFD32F2F),
        onPrimaryClick = onConfirm,
        secondaryButtonText = "No, Stay",
        secondaryButtonColor = Color(0xFF3366CC),
        onSecondaryClick = onDismiss,
        onDismiss = onDismiss
    )
}
