package com.findit.app.presentation.admin

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.findit.app.data.remote.MockData
import com.findit.app.domain.model.Item
import com.findit.app.domain.model.ItemStatus
import com.findit.app.presentation.common.components.StatusBadge
import com.findit.app.presentation.common.components.BadgeSize
import com.findit.app.navigation.Routes
import com.findit.app.presentation.home.HomeViewModel
import com.findit.app.ui.theme.*

@Composable
fun AdminDashboardScreen(navController: NavController) {
    val homeViewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(com.findit.app.di.AppModule.itemRepository) as T
            }
        }
    )

    val liveItems by homeViewModel.items.collectAsState()
    var removedItemIds by remember { mutableStateOf(setOf<String>()) }
    var resolvedItemIds by remember { mutableStateOf(setOf<String>()) }

    val baseItems = if (liveItems.isNotEmpty()) liveItems else MockData.items
    val allItems = baseItems.filter { !removedItemIds.contains(it.id) }.map { item ->
        if (resolvedItemIds.contains(item.id)) item.copy(status = ItemStatus.RESOLVED) else item
    }

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
                                .clickable { tab = i }
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
                0 -> DashboardTab(allItems)
                1 -> ReportsTab(
                    items = allItems,
                    navController = navController,
                    onRemove = { id -> removedItemIds = removedItemIds + id },
                    onResolve = { id -> resolvedItemIds = resolvedItemIds + id }
                )
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
private fun DashboardTab(items: List<Item>) {
    val pendingClaimsCount = MockData.adminClaims.size
    val totalReportsCount = items.size
    val stats = listOf(
        Triple("$totalReportsCount", "Total Reports", "Live Active Reports"),
        Triple("$pendingClaimsCount", "Pending Claims", "Under Review"),
        Triple("234", "Registered Users", "Active School Users"),
        Triple("0", "Flagged Content", "System Clean")
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
                            "approved" -> ActivityStyle("✔", Green100, Green600)
                            "report" -> ActivityStyle("+", Violet100, Violet600)
                            "flag" -> ActivityStyle("⚠️", Amber100, Amber600)
                            "rejected" -> ActivityStyle("✖", Red100, Red500)
                            else -> ActivityStyle("•", Gray100, Gray500)
                        }
                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(50)).background(style.bg), contentAlignment = Alignment.Center) {
                                Text(style.icon, fontSize = 14.sp, color = style.text)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(a["action"] ?: "", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gray900)
                                Text(a["item"] ?: "", fontSize = 12.sp, color = Gray500, maxLines = 1)
                                Text("${a["user"]} · ${a["time"]}", fontSize = 10.sp, color = Gray400)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportsTab(
    items: List<Item>,
    navController: NavController,
    onRemove: (String) -> Unit,
    onResolve: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("${items.size} total reports", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray500)
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Violet50)) {
                Text("Export CSV", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Violet600)
            }
        }

        if (items.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White)) {
                Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📋", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No Reports Found", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Gray700)
                    Text("Reports submitted by users will appear here", fontSize = 12.sp, color = Gray400)
                }
            }
        } else {
            items.forEach { item ->
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AsyncImage(
                            model = item.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1512054502232-10a0a035d672?w=400&h=300&fit=crop&auto=format" },
                            contentDescription = item.name,
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray900, modifier = Modifier.weight(1f))
                                StatusBadge(status = item.status, size = BadgeSize.XS)
                            }
                            Text("${item.reportedBy} · ${item.date}", fontSize = 12.sp, color = Gray400)
                            Text(item.location, fontSize = 12.sp, color = Gray400, maxLines = 1)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Violet50),
                                    modifier = Modifier.clickable { navController.navigate(Routes.itemDetails(item.id)) }
                                ) {
                                    Text("View", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Violet600)
                                }
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Red50),
                                    modifier = Modifier.clickable { onRemove(item.id) }
                                ) {
                                    Text("Remove", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Red500)
                                }
                                if (item.status != ItemStatus.RESOLVED) {
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(containerColor = Gray100),
                                        modifier = Modifier.clickable { onResolve(item.id) }
                                    ) {
                                        Text("Mark Resolved", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gray500)
                                    }
                                }
                            }
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
                    Text("Claimant: ${claim["claimant"]} · ${claim["when"]}", fontSize = 12.sp, color = Gray500)
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
                    if (approved) { Spacer(modifier = Modifier.height(12.dp)); Text("✔ Approved — notifying claimant to collect item", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Green600) }
                    if (rejected) { Spacer(modifier = Modifier.height(12.dp)); Text("✖ Rejected — claimant has been notified", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Red500) }
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
