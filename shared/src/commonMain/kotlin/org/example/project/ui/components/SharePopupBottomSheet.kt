package org.example.project.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_whatsapp
import instaresolv.shared.generated.resources.shareDocument
import instaresolv.shared.generated.resources.textFormat
import instaresolv.shared.generated.resources.txt
import instaresolv.shared.generated.resources.pdf
import instaresolv.shared.generated.resources.pdfFormat
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SharePopupDialog(
    onDismiss: () -> Unit,
    onTxtClick: () -> Unit,
    onPdfClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_whatsapp),
                    contentDescription = null,
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stringResource(Res.string.shareDocument),
                    style = textStyle(size = 19.sp, weight = FontWeight.Bold),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onTxtClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.2.dp, Color(0xFF2962FF)),
                    elevation = null
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.txt),
                            style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                            color = Color(0xFF2962FF)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(Res.string.textFormat),
                            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                            color = Color(0xFF2962FF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onPdfClick()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.2.dp, Color(0xFFD32F2F)),
                    elevation = null
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(Res.string.pdf),
                            style = textStyle(size = 14.sp, weight = FontWeight.Bold),
                            color = Color(0xFFD32F2F)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(Res.string.pdfFormat),
                            style = textStyle(size = 14.sp, weight = FontWeight.SemiBold),
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SharePopupBottomSheet(
    onDismiss: () -> Unit,
    onTxtClick: () -> Unit,
    onPdfClick: () -> Unit
) {
    SharePopupDialog(
        onDismiss = onDismiss,
        onTxtClick = onTxtClick,
        onPdfClick = onPdfClick
    )
}
