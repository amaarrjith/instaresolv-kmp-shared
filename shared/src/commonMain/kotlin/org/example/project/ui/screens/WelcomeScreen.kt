package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import org.jetbrains.compose.resources.painterResource
import org.example.project.localization.LocalAppStrings
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import instaresolv.shared.generated.resources.Res
import instaresolv.shared.generated.resources.bg_welcome_screen
import instaresolv.shared.generated.resources.img_insta_resolv
import instaresolv.shared.generated.resources.ic_arrow_left_red
import org.example.project.model.WelcomeScreenContent
import org.example.project.typography.interFontFamily
import kotlinx.coroutines.launch
import org.example.project.typography.textStyle
import org.koin.compose.koinInject
import org.example.project.welcomescreen.WelcomeScreenViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
) {
    val viewModel: WelcomeScreenViewModel = koinInject()
    val scope = rememberCoroutineScope()
    val strings = LocalAppStrings.current

    val pages = listOf(

        WelcomeScreenContent(
            title = strings.welcomeToInstaresolv,
            description = strings.loremIpsum
        ),
        WelcomeScreenContent(
            title = strings.dimentumAliquam + strings.donecPosuerunc,
            description = strings.loremIpsum
        ),
        WelcomeScreenContent(
            title = strings.donecPosuerunc + strings.dimentumAliquam,
            description = strings.loremIpsum
        )
    )

    val pagerState = rememberPagerState {
        pages.size
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {


        Column(
            modifier = Modifier.background(
                color = Color(0xFFEFF0F5)
            )
        ) {

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Image(
                        painter = painterResource(Res.drawable.bg_welcome_screen),
                        contentDescription = "",
                        modifier = Modifier.padding(horizontal = 35.dp)
                    )

                }

            }

        }



        Surface(

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(.35f),

            shape = BottomSheetCurve(),

            color = Color.White
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = 32.dp,
                        vertical = 40.dp
                    ),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (pagerState.currentPage == 0) {
                    Text(
                        strings.welcomeTo,
                        style = textStyle(
                            size = 14.sp,
                            weight = FontWeight.Normal,
                            color = Color.Gray,
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    Image(
                        painter = painterResource(
                            Res.drawable.img_insta_resolv
                        ),
                        contentDescription = null,
                        modifier = Modifier.height(35.dp)
                    )
                } else {
                    Text(
                        text =
                            pagerState.currentPage.let { pages[it] }.title,

                        textAlign = TextAlign.Center,
                        style = textStyle(
                            size = 18.sp,
                            weight = FontWeight.Bold,
                            color = Color.Black,
                        ),
                        modifier = Modifier.padding(top = 20.dp)
                    )

                }


                Spacer(Modifier.height(20.dp))

                Text(
                    text =
                        pagerState.currentPage.let { pages[it] }.description,

                    textAlign = TextAlign.Center,
                    style = textStyle(
                        size = 14.sp,
                        weight = FontWeight.Normal,
                        color = Color.Gray,
                    ),
                )

                Spacer(Modifier.weight(1f))



                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp)
                ) {

                    Row(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    ) {

                        repeat(3) { index ->

                            Box(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(

                                        if (
                                            pagerState.currentPage ==
                                            index
                                        )
                                            Color.Red
                                        else
                                            Color.LightGray

                                    )
                            )

                        }

                    }

                    Image(

                        painter = painterResource(
                            Res.drawable.ic_arrow_left_red
                        ),

                        contentDescription = null,

                        modifier = Modifier
                            .size(55.dp)
                            .align(
                                Alignment.CenterEnd
                            )
                            .clickable {

                                scope.launch {

                                    val nextPage =
                                        pagerState.currentPage + 1

                                    if (
                                        nextPage <
                                        pagerState.pageCount
                                    ) {

                                        pagerState.animateScrollToPage(
                                            nextPage
                                        )

                                    } else {
                                        viewModel.saveWelcomeScreenStatus()
                                        onNavigateToLogin()
                                    }

                                }
                            }

                    )

                }


            }

        }

    }
}

class BottomSheetCurve : Shape {

    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {

        val path = Path().apply {

            moveTo(0f, 0f)

            quadraticBezierTo(
                size.width / 2f,
                180f,
                size.width,
                0f
            )

            lineTo(size.width, size.height)

            lineTo(0f, size.height)

            close()
        }

        return Outline.Generic(path)
    }

}
