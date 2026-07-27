package org.example.project.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import org.jetbrains.compose.resources.stringResource
import instaresolv.shared.generated.resources.*

@Composable
fun RequestDeleteObservationView(
    onBackClicked: () -> Unit,
    onContinueClicked: (String) -> Unit
) {
    var justification by remember { mutableStateOf("") }
    var errorMesssage by remember { mutableStateOf("") }
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

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
            text = stringResource(Res.string.requestToDeleteObservation1),
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
        
        AppMultilineTextField(
            title = stringResource(Res.string.description),
            value = justification,
            onValueChange = {
                errorMesssage = ""
                justification = it },
            placeholder = stringResource(Res.string.enterDescription)
        )

        if (errorMesssage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorMesssage,
                style = textStyle(size = 12.sp, weight = FontWeight.Normal),
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    if (justification.isEmpty()) {
                        errorMesssage = "Description is required."
                        return@Button
                    }
                    onContinueClicked(justification)
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
}
