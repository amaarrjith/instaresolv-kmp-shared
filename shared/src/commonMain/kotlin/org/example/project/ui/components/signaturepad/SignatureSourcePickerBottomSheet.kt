package org.example.project.ui.components.signaturepad

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_edit
import instaresolv.shared.generated.resources.ic_gallery
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.Image
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureSourcePickerBottomSheet(
    showSheet: Boolean,
    onDismissRequest: () -> Unit,
    onDrawClick: () -> Unit,
    onUploadClick: () -> Unit
) {
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Add Signature",
                    style = textStyle(
                        size = 18.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.BlackText
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { 
                            onDismissRequest()
                            onDrawClick()
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFFFEBEE), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_edit),
                                contentDescription = "Draw",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Draw",
                            style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                            color = AppColors.BlackText
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { 
                            onDismissRequest()
                            onUploadClick()
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFFFEBEE), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_gallery),
                                contentDescription = "Upload",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Upload",
                            style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                            color = AppColors.BlackText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
