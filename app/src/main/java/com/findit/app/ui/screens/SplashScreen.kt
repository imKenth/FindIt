package com.findit.app.ui.screens

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.findit.app.ui.navigation.Routes
import com.findit.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        delay(2800)
        navController.navigate(Routes.LOGIN) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Violet700),
        contentAlignment = Alignment.Center
    ) {
        // Decorative circles
        Box(modifier = Modifier.offset(x = 120.dp, y = (-80).dp).size(288.dp).clip(RoundedCornerShape(50)).background(Violet600.copy(alpha = 0.7f)))
        Box(modifier = Modifier.offset(x = (-120).dp, y = 60.dp).size(208.dp).clip(RoundedCornerShape(50)).background(Violet800.copy(alpha = 0.5f)))
        Box(modifier = Modifier.offset(x = 100.dp, y = 200.dp).size(240.dp).clip(RoundedCornerShape(50)).background(Violet900.copy(alpha = 0.6f)))
        Box(modifier = Modifier.offset(x = (-80).dp, y = 180.dp).size(144.dp).clip(RoundedCornerShape(50)).background(Amber400.copy(alpha = 0.2f)))

        // Accent dots
        Box(modifier = Modifier.offset(x = 100.dp, y = (-150).dp).size(16.dp).clip(RoundedCornerShape(50)).background(Amber300.copy(alpha = 0.7f)))
        Box(modifier = Modifier.offset(x = (-100).dp, y = 0.dp).size(12.dp).clip(RoundedCornerShape(50)).background(White.copy(alpha = 0.3f)))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .shadow(32.dp, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
                    .background(White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Fi",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = OutfitFontFamily,
                    color = Violet600
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Wordmark
            Text(
                text = "FindIt",
                fontSize = 56.sp,
                fontWeight = FontWeight.Black,
                fontFamily = OutfitFontFamily,
                color = White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Lost it? Find it.",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Violet200
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your school's digital lost & found",
                fontSize = 14.sp,
                color = Violet300.copy(alpha = 0.8f)
            )
        }

        // Loading dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(White.copy(alpha = 0.7f * alpha))
                )
            }
        }
    }
}
