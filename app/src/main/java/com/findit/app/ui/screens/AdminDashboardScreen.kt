package com.findit.app.ui.screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.findit.app.data.mock.MockData
import com.findit.app.data.model.ItemStatus
import com.findit.app.ui.components.StatusBadge
import com.findit.app.ui.components.BadgeSize
import com.findit.app.ui.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun AdminDashboardScreen(navController: NavController) {
    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dashboard", "Reports", "Claims", "Users")
    var approvedClaims by remember { mutableStateOf(setOf<String>()) }
    var rejectedClaims by remember { mutableStateOf(setOf<String>()) }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Gray900)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(Amber400), contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Shield, contentDescription = null, tint = Gray900, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Admin Panel", fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = White)
                            Text("FindIt School Administrator", fontSize = 12.sp, color = Gray400)
                        }
                    }
                    TextButton(onClick = { navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } }) {
                        Text("Sign out", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gray400)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    tabs.forEachIndexed { i, t ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                .background(if (tab == i) Gray50 else Color.Transparent)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(t, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (tab == i) Gray900 else Gray500)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Gray50)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            when (tab) {
                0 -> DashboardTab()
                1 -> ReportsTab()
                2 -> ClaimsTab(
                    approvedClaims,
                    rejectedClaims,
                    { approvedClaims = it },
                    { rejectedClaims = it },
                )
                3 -> UsersTab()
            }
        }
    }
}

@Composable
private fun DashboardTab() {
    val stats = listOf(
        Triple("47", "Total Reports", "+5 today"),
        Triple("12", "Pending Claims", "3 urgent"),
        Triple("234", "Registered Users", "+8 this week"),
        Triple("8", "Flagged Content", "Needs review")
    )

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Stats grid
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            stats.take(2).forEach { (value, label, change) ->
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gray400)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(value, fontSize = 32.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Violet100)) {
                            Text(change, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Violet700)
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            stats.drop(2).forEach { (value, label, change) ->
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gray400)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(value, fontSize = 32.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                        Spacer(modifier = Modifier.height(6.dp))
                        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Violet100)) {
                            Text(change, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Violet700)
                        }
                    }
                }
            }
        }

        // Activity
        Column {
            Text("Recent Activity", fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
            Spacer(modifier = Modifier.height(12.dp))
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White)) {
                Column {
                    MockData.adminActivity.forEach { a ->
                        data class ActivityStyle(val icon: String, val bg: Color, val text: Color)
                        val style = when (a["type"]) {
                            "approved" -> ActivityStyle("\u2714", Green100, Green600)
                            "report" -> ActivityStyle("+", Violet100, Violet600)
                            "flag" -> ActivityStyle("\u26A0", Amber100, Amber600)
                            "rejected" -> ActivityStyle("\u2716", Red100, Red500)
                            else -> ActivityStyle("\u2022", Gray100, Gray500)
                        }
                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(50)).background(style.bg), contentAlignment = Alignment.Center) {
                                Text(style.icon, fontSize = 14.sp, color = style.text)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(a["action"] ?: "", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gray900)
                                Text(a["item"] ?: "", fontSize = 12.sp, color = Gray500, maxLines = 1)
                                Text("${a["user"]} \u00B7 ${a["time"]}", fontSize = 10.sp, color = Gray400)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportsTab() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("${MockData.items.size} total reports", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray500)
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Violet50)) {
                Text("Export CSV", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Violet600)
            }
        }
        MockData.items.forEach { item ->
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AsyncImage(model = item.imageUrl, contentDescription = item.name, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray900, modifier = Modifier.weight(1f))
                            StatusBadge(status = item.status, size = BadgeSize.XS)
                        }
                        Text("${item.reportedBy} \u00B7 ${item.date}", fontSize = 12.sp, color = Gray400)
                        Text(item.location, fontSize = 12.sp, color = Gray400, maxLines = 1)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Violet50)) { Text("View", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Violet600) }
                            Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Red50)) { Text("Remove", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Red500) }
                            Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Gray100)) { Text("Mark Resolved", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gray500) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClaimsTab(approvedClaims: Set<String>, rejectedClaims: Set<String>, onApproved: (Set<String>) -> Unit, onRejected: (Set<String>) -> Unit) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("${MockData.adminClaims.size} pending claims", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray500)
        MockData.adminClaims.forEach { claim ->
            val id = claim["id"] ?: ""
            val approved = approvedClaims.contains(id)
            val rejected = rejectedClaims.contains(id)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(claim["item"] ?: "", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray900)
                        StatusBadge(status = when { approved -> ItemStatus.APPROVED; rejected -> ItemStatus.REJECTED; else -> ItemStatus.PENDING }, size = BadgeSize.XS)
                    }
                    Text("Claimant: ${claim["claimant"]} \u00B7 ${claim["when"]}", fontSize = 12.sp, color = Gray500)
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Gray50)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("PRIVATE IDENTIFYING DETAIL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Gray400, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("\"${claim["detail"]}\"", fontSize = 12.sp, color = Gray700)
                        }
                    }
                    if (!approved && !rejected) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { onApproved(approvedClaims + id) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Green500)) {
                                Text("Approve", fontWeight = FontWeight.Black)
                            }
                            OutlinedButton(onClick = { onRejected(rejectedClaims + id) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Red600), border = BorderStroke(1.dp, Red200)) {
                                Text("Reject", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (approved) { Spacer(modifier = Modifier.height(12.dp)); Text("\u2714 Approved \u2014 notifying claimant to collect item", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Green600) }
                    if (rejected) { Spacer(modifier = Modifier.height(12.dp)); Text("\u2716 Rejected \u2014 claimant has been notified", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Red500) }
                }
            }
        }
    }
}

@Composable
private fun UsersTab() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("${MockData.adminUsers.size} registered users", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray500)
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Violet50)) {
                Text("Invite User", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Violet600)
            }
        }
        MockData.adminUsers.forEach { u ->
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(Violet100), contentAlignment = Alignment.Center) {
                        Text((u["name"] as? String)?.first()?.toString() ?: "", fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Violet600)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(u["name"] as? String ?: "", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray900)
                        Text(u["email"] as? String ?: "", fontSize = 12.sp, color = Gray400, maxLines = 1)
                        Text("${u["reports"]} reports", fontSize = 12.sp, color = Gray400)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        val isActive = u["status"] == "Active"
                        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = if (isActive) Green100 else Red100)) {
                            Text(u["status"] as? String ?: "", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isActive) Green600 else Red500)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(if (isActive) "Suspend" else "Restore", fontSize = 11.sp, color = Gray400)
                    }
                }
            }
        }
    }
}
