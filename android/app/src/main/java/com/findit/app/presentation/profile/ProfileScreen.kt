package com.findit.app.presentation.profile

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.findit.app.data.remote.MockData
import com.findit.app.presentation.common.components.BottomNav
import com.findit.app.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun ProfileScreen(navController: NavController) {
    val user = MockData.currentUser
    val unread = MockData.notifications.count { !it.read }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Scaffold(
        bottomBar = { BottomNav(navController = navController, activeRoute = Routes.PROFILE, notifCount = unread) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Violet50)
                .consumeWindowInsets(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Violet header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Violet700)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Box(modifier = Modifier.offset(x = 120.dp, y = (-40).dp).size(144.dp).clip(RoundedCornerShape(50)).background(Violet600.copy(alpha = 0.5f)))
                Box(modifier = Modifier.offset(x = (-80).dp, y = 40.dp).size(112.dp).clip(RoundedCornerShape(50)).background(Violet800.copy(alpha = 0.4f)))
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Profile", fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = White)
                        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.15f))) {
                            Text("Edit Profile", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Violet200)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AsyncImage(model = user.avatar, contentDescription = user.name, modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)).shadow(8.dp, RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.name, fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = White)
                            Text(user.email, fontSize = 14.sp, color = Violet200, maxLines = 1)
                            Spacer(modifier = Modifier.height(8.dp))
                            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Emerald400.copy(alpha = 0.2f))) {
                                Text("\u2713 Verified Student", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Emerald300)
                            }
                        }
                    }
                }
            }

            // Stats card
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).offset(y = (-20).dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    listOf(user.reportsCount to "Reports", user.claimsCount to "Claims", user.resolvedCount to "Resolved").forEach { (v, l) ->
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("\uD83D\uDCCB", fontSize = 16.sp)
                            Text("$v", fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Violet700)
                            Text(l, fontSize = 10.sp, color = Gray400, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Menu groups
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Main menu
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column {
                        ProfileMenuItem("\uD83D\uDCCB", "My Reports", "${user.reportsCount} active reports") { navController.navigate(Routes.MY_REPORTS) }
                        HorizontalDivider(color = Gray50)
                        ProfileMenuItem("\uD83D\uDD14", "Notifications", if (unread > 0) "$unread unread notifications" else "All caught up") { navController.navigate(Routes.NOTIFICATIONS) }
                        HorizontalDivider(color = Gray50)
                        ProfileMenuItem("\u2699\uFE0F", "Settings", "Privacy & preferences") { }
                        HorizontalDivider(color = Gray50)
                        ProfileMenuItem("\u2753", "Help & Support", "FAQs, contact admin") { }
                    }
                }

                // Admin access
                Card(
                    modifier = Modifier.clickable { navController.navigate(Routes.ADMIN) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Amber50)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Amber100), contentAlignment = Alignment.Center) {
                            Text("\uD83D\uDEE1\uFE0F", fontSize = 20.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Admin Panel", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Amber900)
                            Text("Manage reports, claims & users", fontSize = 12.sp, color = Amber600)
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Amber400)
                    }
                }

                // Logout
                OutlinedButton(
                    onClick = { navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Red500),
                    border = BorderStroke(1.dp, Red100)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: String, label: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Violet100), contentAlignment = Alignment.Center) {
            Text(icon, fontSize = 20.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray900)
            Text(subtitle, fontSize = 12.sp, color = Gray400)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Gray300)
    }
}
