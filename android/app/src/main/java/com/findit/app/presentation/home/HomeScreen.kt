package com.findit.app.presentation.home

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.findit.app.data.remote.MockData
import com.findit.app.domain.model.ItemStatus
import com.findit.app.presentation.auth.AuthViewModel
import com.findit.app.presentation.common.components.BottomNav
import com.findit.app.presentation.common.components.ItemCard
import com.findit.app.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {

    val viewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(com.findit.app.di.AppModule.itemRepository) as T
            }
        }
    )

    val authViewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(com.findit.app.di.AppModule.authRepository) as T
            }
        }
    )

    val currentUser by authViewModel.currentUser.collectAsState()
    val liveItems by viewModel.items.collectAsState()
    val allItems = if (liveItems.isNotEmpty()) liveItems else MockData.items

    val unread = MockData.notifications.count { !it.read }
    val matches = allItems.filter { it.status == ItemStatus.FOUND }.take(4)
    val recent = allItems.take(6)
    val userName = currentUser?.name ?: MockData.currentUser.name

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Scaffold(
        bottomBar = { BottomNav(navController = navController, activeRoute = Routes.HOME, notifCount = unread) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Violet50)
                .consumeWindowInsets(padding)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Violet700)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(12.dp)).background(White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Fi", fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Violet600)
                            }
                            Text("FindIt", fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = White)
                        }
                        BadgedBox(
                            badge = { if (unread > 0) Badge { Text(unread.toString()) } }
                        ) {
                            IconButton(
                                onClick = { navController.navigate(Routes.NOTIFICATIONS) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Violet600.copy(alpha = 0.3f))
                            ) {
                                Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Good day,", fontSize = 14.sp, color = White)
                    Text(
                        text = "$userName 👋",
                        fontSize = 22.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = White
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Search bar
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Routes.SEARCH) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.95f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = Gray400, modifier = Modifier.size(16.dp))
                            Text("Search lost & found items...", fontSize = 14.sp, color = Gray400)
                        }
                    }
                }
            }

            // Scrollable body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Quick Actions
                Text("Quick Actions", fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f).clickable { navController.navigate(Routes.REPORT_LOST) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Red500),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✖", color = White, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Report Lost", fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = White)
                            Text("Lost something?", fontSize = 12.sp, color = White.copy(alpha = 0.9f))
                        }
                    }
                    Card(
                        modifier = Modifier.weight(1f).clickable { navController.navigate(Routes.REPORT_FOUND) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Green500),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✔", color = White, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Report Found", fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = White)
                            Text("Found something?", fontSize = 12.sp, color = White.copy(alpha = 0.9f))
                        }
                    }
                }

                // Stats
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        listOf("${allItems.size}" to "Reports", "${allItems.count { it.status == ItemStatus.FOUND }}" to "Found today", "${allItems.count { it.status == ItemStatus.LOST }}" to "Active lost").forEach { (n, label) ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(n, fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Violet700)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(label, fontSize = 10.sp, color = Gray500, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }

                // Possible Matches
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Possible Matches", fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                            Text("Based on active reports", fontSize = 12.sp, color = Gray500)
                        }
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Violet50),
                            modifier = Modifier.clickable { navController.navigate(Routes.SEARCH) }
                        ) {
                            Text("See all", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Violet600)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(matches) { item ->
                            ItemCard(
                                item = item,
                                compact = true,
                                onClick = { navController.navigate(Routes.itemDetails(item.id)) }
                            )
                        }
                    }
                }

                // Recent Reports
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Recent Reports", fontSize = 15.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                            Text("Latest from your school", fontSize = 12.sp, color = Gray500)
                        }
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Violet50),
                            modifier = Modifier.clickable { navController.navigate(Routes.SEARCH) }
                        ) {
                            Text("See all", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Violet600)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    recent.forEach { item ->
                        ItemCard(
                            item = item,
                            onClick = { navController.navigate(Routes.itemDetails(item.id)) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
