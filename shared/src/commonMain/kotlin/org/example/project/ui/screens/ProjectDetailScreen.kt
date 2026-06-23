package org.example.project.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_add
import instaresolv.shared.generated.resources.ic_add_plus
import instaresolv.shared.generated.resources.ic_edit
import instaresolv.shared.generated.resources.ic_email
import instaresolv.shared.generated.resources.ic_search
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.ui.components.WebImageView
import org.example.project.utilites.AppBorderButton
import org.example.project.utilites.NavigationBackIcon
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProjectDetailScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            ProjectDetailScreenTopBar(
                onEditClick = {},
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                WebImageView(
                    imageUrl = "https://picsum.photos/200/300",
                    modifier = Modifier.fillMaxWidth()
                        .height(220.dp)
                )
                Spacer(modifier = Modifier.height(22.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Gray)
                    ) {
                        Text(
                            text = "CN-0422-B",
                            modifier = Modifier.padding(vertical = 2.dp, horizontal = 6.dp),
                            style = textStyle(size = 10.sp, weight = FontWeight.SemiBold),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Water pipe broken in the 2nd floor bathroom",
                        style = textStyle(
                            size = 16.sp,
                            weight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Lorem ipsum dolor sit amet consectetur. Vestibulum enim massa amet ut. Consequat est eleifend facilisis feugiat. Enim eget aliquam blandit viverra nisl egestas. Sagittis in id at tempor nunc.",
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Normal
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        Text(
                            text = "Project Members",
                            style = textStyle(
                                size = 14.sp,
                                weight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(Res.drawable.ic_search),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        AddMemberIcon()
                    }
                    ProjectMembersScreen()
                }
            }
    }
}

@Composable
fun ProjectMembersScreen() {
    Column {
        repeat(10) { index ->
            ProjectMembersItemRow()
        }
    }
}

@Composable
fun AddMemberIcon() {
    Row() {
        Image(
            painter = painterResource(Res.drawable.ic_add_plus),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Member",
            style = textStyle(
                size = 13.sp,
                weight = FontWeight.SemiBold
            ),
            color = AppColors.Primary
        )
    }
}

@Composable
fun ProjectMembersItemRow() {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WebImageView(
            imageUrl = "https://picsum.photos/200/300",
            modifier = Modifier.size(50.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "John Doe",
                style = textStyle(
                    size = 14.sp,
                    weight = FontWeight.Bold
                )
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_email),
                    contentDescription = null
                )
                Text(
                    text = "Lincoln@yopmail.com",
                    style = textStyle(
                        size = 12.sp,
                        weight = FontWeight.Normal
                    ),
                    color = AppColors.TextGray
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        MemberStatusIcon(isAdmin = false)

    }
}

@Composable
fun MemberStatusIcon(isAdmin: Boolean) {
    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = if (isAdmin) AppColors.Primary else AppColors.TextGray,
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (isAdmin) "ADMIN" else "PARTICIPANT",
            style = textStyle(
                size = 10.sp,
                weight = FontWeight.SemiBold
            ),
            color = AppColors.TextGray
        )
    }
}

@Composable
fun ProjectDetailScreenTopBar(
    onEditClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .statusBarsPadding()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationBackIcon(
            onClick = {
                onBackClick()
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Project".uppercase(),
            style = textStyle(
                size = 14.sp,
                weight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .padding(end = 26.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_edit),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Edit".uppercase(),
                modifier = Modifier.clickable {
                    onEditClick()
                },
                style = textStyle(
                    size = 13.sp,
                    weight = FontWeight.SemiBold
                ),
                color = AppColors.SkyBlue
            )
        }
    }
}

@Composable
@Preview
fun ProjectDetailScreenPreview() {
    ProjectDetailScreen(
        onBackClick = {}
    )
}