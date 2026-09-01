package com.findit.app.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.findit.app.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun ReportTypeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                IconButton(onClick = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) } }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gray600)
                }
                Text("Report an Item", fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Violet100),
                    contentAlignment = Alignment.Center
                ) {
                    Text("\u2753", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("What happened?", fontSize = 26.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Tell us whether you lost something or you found something that belongs to someone else.",
                    fontSize = 14.sp, color = Gray400, lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Lost option
            Card(
                modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Routes.REPORT_LOST) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Red50),
                border = CardDefaults.outlinedCardBorder().copy(width = 2.dp, brush = androidx.compose.ui.graphics.SolidColor(Red200))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(Red500),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("\u2716", fontSize = 24.sp, color = White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("I Lost Something", fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                        Text("Report an item and we'll help you find it", fontSize = 14.sp, color = Gray500)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Red400)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Found option
            Card(
                modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Routes.REPORT_FOUND) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Green50),
                border = CardDefaults.outlinedCardBorder().copy(width = 2.dp, brush = androidx.compose.ui.graphics.SolidColor(Green200))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(Green500),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("\u2714", fontSize = 24.sp, color = White)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("I Found Something", fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                        Text("Help return an item to its rightful owner", fontSize = 14.sp, color = Gray500)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Green400)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Privacy note
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Violet50)
            ) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("\u2139\uFE0F", fontSize = 16.sp)
                    Text(
                        "Reports are visible to all students. Don't include sensitive personal info (ID numbers, PINs) in public descriptions.",
                        fontSize = 12.sp, color = Violet700, lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
