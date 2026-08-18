package org.example.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.colors.AppColors
import org.example.project.data.model.GroupUser
import org.example.project.typography.textStyle
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*
import org.example.project.utilites.ToastHost
import org.example.project.utilites.ToastType
import org.example.project.utilites.ToastView

@Composable
fun RequestResponsiblePersonChangeView(
    users: List<GroupUser>,
    onBackClicked: () -> Unit,
    onContinueClicked: (justification: String, responsiblePersonId: Int) -> Unit
) {
    var justification by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<GroupUser?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    Box() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.openObservation),
                    style = textStyle(size = 18.sp, weight = FontWeight.Bold),
                    color = AppColors.Black
                )
                Text(
                    text = stringResource(Res.string.back),
                    style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                    color = AppColors.Primary,
                    modifier = Modifier.clickable { onBackClicked() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(Res.string.requestObservationResponsiblenpersonChange),
                style = textStyle(size = 16.sp, weight = FontWeight.Medium),
                color = AppColors.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(Res.string.provideAShortDescriptionOfWhyYouAreRequestingThisObservationToBeChanged),
                style = textStyle(size = 14.sp, weight = FontWeight.Normal),
                color = AppColors.TextGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            AppUserDropdown(
                title = stringResource(Res.string.responsiblePerson),
                isMandatory = true,
                placeholder = stringResource(Res.string.selectResponsiblePerson),
                users = users,
                selectedUser = selectedUser,
                onUserSelected = {
                    errorMessage = ""
                    selectedUser = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppMultilineTextField(
                title = stringResource(Res.string.description),
                value = justification,
                onValueChange = {
                    errorMessage = ""
                    justification = it
                },
                placeholder = stringResource(Res.string.enterDescription)
            )



            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val selectResponsiblePersonError =
                    stringResource(Res.string.select_responsible_person_error)
                val descriptionRequiredError = stringResource(Res.string.description_required_error)

                Button(
                    onClick = {
                        when {
                            selectedUser == null -> {
                                errorMessage = selectResponsiblePersonError
                            }

                            justification.isEmpty() -> {
                                errorMessage = descriptionRequiredError
                            }

                            else -> {
                                onContinueClicked(justification, selectedUser!!.userId)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.continueAction),
                        style = textStyle(size = 14.sp, weight = FontWeight.Medium),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        ToastHost(
            visible = errorMessage.isNotEmpty(),
            type = ToastType.Error,
            message = errorMessage,
            onDismiss = {
                errorMessage = ""
            },
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}
