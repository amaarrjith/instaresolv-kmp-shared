package org.example.project.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.closeObservation
import instaresolv.shared.generated.resources.generatePdf
import instaresolv.shared.generated.resources.ic_right_icon
import instaresolv.shared.generated.resources.openObservation
import instaresolv.shared.generated.resources.requestObservationResponsiblenpersonChange
import instaresolv.shared.generated.resources.requestToDeleteObservation
import instaresolv.shared.generated.resources.requestToJoinProject
import instaresolv.shared.generated.resources.observationResponsibilityChange
import instaresolv.shared.generated.resources.reviewObservationCloseout
import instaresolv.shared.generated.resources.viewReport
import instaresolv.shared.generated.resources.viewObservation
import instaresolv.shared.generated.resources.viewJustification
import instaresolv.shared.generated.resources.viewProject
import instaresolv.shared.generated.resources.viewProfile
import instaresolv.shared.generated.resources.viewObservationCloseout
import instaresolv.shared.generated.resources.approve
import instaresolv.shared.generated.resources.reject
import org.example.project.colors.AppColors
import org.example.project.data.model.PendingActionStatusType
import org.example.project.typography.textStyle
import org.example.project.utilites.rtlScale
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObservationActionBottomSheet(
    showSheet: Boolean,
    actionType: Int = PendingActionStatusType.OPEN_OBSERVATION,
    onDismiss: () -> Unit,
    onActionClick: (String) -> Unit
) {

    if (!showSheet) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            val title = when (actionType) {
                PendingActionStatusType.OPEN_OBSERVATION -> stringResource(Res.string.openObservation)
                PendingActionStatusType.REQUEST_TO_JOIN_GROUP -> stringResource(Res.string.requestToJoinProject)
                PendingActionStatusType.OBSERVATION_RESPONSIBILITY_CHANGE -> stringResource(Res.string.observationResponsibilityChange)
                PendingActionStatusType.REQUEST_TO_DELETE_OBSERVATION -> stringResource(Res.string.requestToDeleteObservation)
                PendingActionStatusType.REVIEW_OBSERVATION_CLOSEOUT -> stringResource(Res.string.reviewObservationCloseout)
                else -> ""
            }

            Text(
                text = title,
                style = textStyle(
                    size = 18.sp,
                    weight = FontWeight.Bold
                ),
                color = AppColors.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (actionType) {
                PendingActionStatusType.OPEN_OBSERVATION -> {
                    val actions = listOf(
                        Pair("View Report", stringResource(Res.string.viewReport)),
                        Pair("Generate PDF", stringResource(Res.string.generatePdf)),
                        Pair("Close Observation", stringResource(Res.string.closeObservation)),
                        Pair("Request Observation Responsible Person Change", stringResource(Res.string.requestObservationResponsiblenpersonChange)),
                        Pair("Request to Delete Observation", stringResource(Res.string.requestToDeleteObservation))
                    )

                    actions.forEach { (id, actionTitle) ->
                        ObservationActionItem(
                            title = actionTitle,
                            onClick = {
                                onActionClick(id)
                                onDismiss()
                            }
                        )
                    }
                }
                PendingActionStatusType.REQUEST_TO_JOIN_GROUP -> {
                    ObservationActionItem(
                        title = stringResource(Res.string.viewProfile),
                        onClick = {
                            onActionClick("View Profile")
                            onDismiss()
                        }
                    )
                    ObservationActionItem(
                        title = stringResource(Res.string.viewProject),
                        onClick = {
                            onActionClick("View Project")
                            onDismiss()
                        }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).clickable { 
                            onActionClick("Reject Join")
                            onDismiss() 
                        }.padding(15.dp), contentAlignment = Alignment.Center) {
                            Text(text = stringResource(Res.string.reject), style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                        }
                        Box(modifier = Modifier.width(1.dp).height(20.dp).background(AppColors.TextGray.copy(alpha = 0.3f)))
                        Box(modifier = Modifier.weight(1f).clickable { 
                            onActionClick("Approve Join")
                            onDismiss() 
                        }.padding(15.dp), contentAlignment = Alignment.Center) {
                            Text(text = stringResource(Res.string.approve), style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.Primary)
                        }
                    }
                }
                PendingActionStatusType.REVIEW_OBSERVATION_CLOSEOUT -> {
                    ObservationActionItem(
                        title = stringResource(Res.string.viewObservationCloseout),
                        onClick = {
                            onActionClick("View Observation Closeout")
                            onDismiss()
                        }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).clickable { 
                            onActionClick("Reject")
                            onDismiss() 
                        }.padding(15.dp), contentAlignment = Alignment.Center) {
                            Text(text = stringResource(Res.string.reject), style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                        }
                        Box(modifier = Modifier.width(1.dp).height(20.dp).background(AppColors.TextGray.copy(alpha = 0.3f)))
                        Box(modifier = Modifier.weight(1f).clickable { 
                            onActionClick("Approve")
                            onDismiss() 
                        }.padding(15.dp), contentAlignment = Alignment.Center) {
                            Text(text = stringResource(Res.string.approve), style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.Primary)
                        }
                    }
                }
                PendingActionStatusType.OBSERVATION_RESPONSIBILITY_CHANGE,
                PendingActionStatusType.REQUEST_TO_DELETE_OBSERVATION -> {
                    ObservationActionItem(
                        title = stringResource(Res.string.viewObservation),
                        onClick = {
                            onActionClick("View Observation")
                            onDismiss()
                        }
                    )
                    ObservationActionItem(
                        title = stringResource(Res.string.viewJustification),
                        onClick = {
                            onActionClick("View Justification")
//                            onDismiss()
                        }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f).clickable { 
                            val actionStr = if (actionType == PendingActionStatusType.OBSERVATION_RESPONSIBILITY_CHANGE) "Reject Change" else "Reject Delete"
                            onActionClick(actionStr)
                            onDismiss() 
                        }.padding(15.dp), contentAlignment = Alignment.Center) {
                            Text(text = stringResource(Res.string.reject), style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.TextGray)
                        }
                        Box(modifier = Modifier.width(1.dp).height(20.dp).background(AppColors.TextGray.copy(alpha = 0.3f)))
                        Box(modifier = Modifier.weight(1f).clickable { 
                            val actionStr = if (actionType == PendingActionStatusType.OBSERVATION_RESPONSIBILITY_CHANGE) "Approve Change" else "Approve Delete"
                            onActionClick(actionStr)
                            onDismiss() 
                        }.padding(15.dp), contentAlignment = Alignment.Center) {
                            Text(text = stringResource(Res.string.approve), style = textStyle(size = 16.sp, weight = FontWeight.Medium), color = AppColors.Primary)
                        }
                    }
                }
                else -> {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ObservationActionItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = textStyle(
                size = 14.sp,
                weight = FontWeight.Medium
            )
        )

        Image(
            modifier = Modifier.padding(
                start = 40.dp
            ).rtlScale(),
            painter = painterResource(Res.drawable.ic_right_icon),
            contentDescription = null
        )
    }
}
