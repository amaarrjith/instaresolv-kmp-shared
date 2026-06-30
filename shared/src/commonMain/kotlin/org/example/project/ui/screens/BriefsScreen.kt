package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.ic_audit_inspection
import instaresolv.shared.generated.resources.ic_incidents
import instaresolv.shared.generated.resources.ic_lesson
import instaresolv.shared.generated.resources.ic_observations
import instaresolv.shared.generated.resources.ic_permit_to_work
import instaresolv.shared.generated.resources.ic_permit_work
import instaresolv.shared.generated.resources.ic_toolbox_talks
import instaresolv.shared.generated.resources.ic_training
import instaresolv.shared.generated.resources.ic_violations
import org.example.project.colors.AppColors
import org.example.project.data.model.ActionsOverview
import org.example.project.typography.textStyle
import org.example.project.ui.ActionOverview
import org.example.project.ui.ActionOverviewCard
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.chunked
import kotlin.collections.forEach

@Composable
fun BriefsScreen(
    actionsOverview: ActionsOverview? = null,
    onPreTaskClicked: () -> Unit = {}
) {
    val items = listOf(
        BriefOverviewItem(BriefOverview.PRE_TASK, actionsOverview?.preTaskCount),
        BriefOverviewItem(BriefOverview.TOOLBOX_TALKS, actionsOverview?.toolboxCount),
        BriefOverviewItem(BriefOverview.LESSON, actionsOverview?.lessonCount)
    )
    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row (
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(vertical = 20.dp),
            ) {
                Text(
                    text = "Briefs".uppercase(),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    ),
                    color = AppColors.Black
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
            ) {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Overview",
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowItems.forEach { action ->
                                Box(modifier = Modifier.weight(1f)) {
                                    BriefOverviewCard(
                                        action.type,
                                        action.count ?: 0,
                                        onClick = {
                                            if (action.type == BriefOverview.PRE_TASK) {
                                                onPreTaskClicked()
                                            }
                                        }
                                    )
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
//                        Spacer(modifier = Modifier.height(12.dp))
                    }

                }
            }
        }
    }
}

enum class BriefOverview(
    val title: String,
    val icon: DrawableResource
) {
    PRE_TASK(
        "Pre Task Briefing",
        Res.drawable.ic_permit_work
    ),

    TOOLBOX_TALKS(
        "Toolbox Talks",
        Res.drawable.ic_toolbox_talks
    ),

    LESSON(
        "Lesson Learned",
        Res.drawable.ic_lesson
    )
}

data class BriefOverviewItem(
    val type: BriefOverview,
    val count: Int?
)


@Composable
fun BriefOverviewCard(
    action: BriefOverview,
    count: Int,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(113.dp)
            .dropShadow(
                shape = RoundedCornerShape(16.dp),
                shadow = Shadow(
                    radius = 20.dp,
                    color = Color(0x0F000000),
                    offset = DpOffset(0.dp, 4.dp)
                )
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Color(0xFFE5E5E5),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = painterResource(action.icon),
                    contentDescription = action.title
                )

                Text(
                    text = count.toString().padStart(2, '0'),
                    style = textStyle(
                        24.sp,
                        FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = action.title,
                style = textStyle(
                    14.sp,
                    FontWeight.SemiBold
                )
            )
        }
    }
}
