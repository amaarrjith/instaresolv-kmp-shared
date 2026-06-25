package org.example.project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.model.GroupUser
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUserDropdown(
    title: String,
    placeholder: String,
    users: List<GroupUser>,
    selectedUser: GroupUser?,
    onUserSelected: (GroupUser) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = textStyle(
                size = 12.sp,
                weight = FontWeight.SemiBold
            ),
            color = AppColors.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp)
                    .menuAnchor(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedUser != null) {
                    WebImageView(
                        imageUrl = selectedUser.image,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEFEFEF))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (selectedUser?.name.isNullOrEmpty()) {
                        Text(
                            text = placeholder,
                            style = textStyle(
                                size = 14.sp,
                                weight = FontWeight.Medium
                            ),
                            color = Color(0xFF9E9E9E)
                        )
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = selectedUser?.name ?: "",
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        readOnly = true,
                        textStyle = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Medium
                        ).copy(color = AppColors.Black),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(AppColors.Primary)
                    )
                }

                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                if (users.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No Users Found") },
                        onClick = { }
                    )
                } else {
                    users.forEach { user ->
                        DropdownMenuItem(
                            text = { 
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    WebImageView(
                                        imageUrl = user.image,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEFEFEF))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = user.name,
                                            style = textStyle(size = 15.sp, weight = FontWeight.Medium),
                                            color = AppColors.Black
                                        )
                                        Text(
                                            text = user.email,
                                            style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                                            color = Color(0xFF8F9098)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                onUserSelected(user)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
