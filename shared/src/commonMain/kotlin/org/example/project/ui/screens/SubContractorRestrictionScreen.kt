package org.example.project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.access_restricted
import instaresolv.shared.generated.resources.access_restricted_description
import instaresolv.shared.generated.resources.ic_permit_restriction
import instaresolv.shared.generated.resources.ic_restriction
import instaresolv.shared.generated.resources.no_project_assigned
import instaresolv.shared.generated.resources.permit_restriction_description
import org.example.project.colors.AppColors
import org.example.project.typography.textStyle
import org.example.project.utilites.NavigationBackIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SubContractorRestrictionScreen(
    isPermitSection: Boolean = false,
    onBackClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 10.dp)
                    .padding(end = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClicked != null) {
                    NavigationBackIcon(onBackClicked)
                }
            }
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(
                        if (isPermitSection) {
                            Res.drawable.ic_permit_restriction
                        } else {
                            Res.drawable.ic_restriction
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.wrapContentSize(),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = stringResource(if (isPermitSection) Res.string.no_project_assigned else Res.string.access_restricted),
                    style = textStyle(
                        size = 18.sp,
                        weight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ),
                    color = AppColors.Primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 21.dp)
                )

                Text(
                    text = stringResource(if (isPermitSection) Res.string.permit_restriction_description else Res.string.access_restricted_description),
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic
                    ),
                    color = AppColors.Primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        top = 10.dp,
                        bottom = 14.dp
                    )
                )
            }
        }
    }
}