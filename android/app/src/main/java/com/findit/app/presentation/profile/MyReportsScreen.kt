package com.findit.app.presentation.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.findit.app.data.remote.MockData
import com.findit.app.domain.model.ItemStatus
import com.findit.app.domain.model.ItemType
import com.findit.app.presentation.auth.AuthViewModel
import com.findit.app.presentation.common.components.BottomNav
import com.findit.app.presentation.common.components.StatusBadge
import com.findit.app.presentation.common.components.BadgeSize
import com.findit.app.navigation.Routes
import com.findit.app.presentation.home.HomeViewModel
import com.findit.app.ui.theme.*

@Composable
fun MyReportsScreen(navController: NavController) {
    val homeViewModel: HomeViewModel = viewModel(
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
    val allItems by homeViewModel.items.collectAsState()

    val currentUserName = currentUser?.name ?: "Alex Rivera"

    val userItems = if (allItems.isNotEmpty()) {
        val userFiltered = allItems.filter { it.reportedBy.equals(currentUserName, ignoreCase = true) }
        if (userFiltered.isNotEmpty()) userFiltered else allItems.take(2)
    } else {
        MockData.myReports
    }

    val activeReports = userItems.filter { it.status == ItemStatus.LOST || it.status == ItemStatus.FOUND }
    val resolvedReports = userItems.filter { it.status == ItemStatus.RESOLVED || it.status == ItemStatus.CLAIMED }
    val myClaims = MockData.myClaims

    var tab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Claims", "Resolved")

    Scaffold(
        bottomBar = { BottomNav(navController = navController, activeRoute = Routes.MY_REPORTS) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .consumeWindowInsets(padding)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("My Reports", fontSize = 24.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    tabs.forEachIndexed { i, t ->
                        val count = when (i) { 0 -> activeReports.size; 1 -> myClaims.size; 2 -> resolvedReports.size; else -> 0 }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { tab = i }
                                .background(if (tab == i) Violet600 else White)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("$t ($count)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (tab == i) White else Gray400)
                        }
                    }
                }
            }

            HorizontalDivider(color = Gray100)

            // Tab content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (tab) {
                    0 -> { // Active
                        if (activeReports.isEmpty()) {
                            EmptyState("No Active Reports", "Report a lost or found item to get started")
                        } else {
                            activeReports.forEach { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Routes.itemDetails(item.id)) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        AsyncImage(
                                            model = item.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1512054502232-10a0a035d672?w=400&h=300&fit=crop&auto=format" },
                                            contentDescription = item.name,
                                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900, modifier = Modifier.weight(1f))
                                                StatusBadge(status = item.status, size = BadgeSize.XS)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("${if (item.type == ItemType.LOST) "Lost" else "Found"} Report", fontSize = 12.sp, color = Violet500, fontWeight = FontWeight.Bold)
                                            Text(item.location, fontSize = 12.sp, color = Gray400)
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Violet50)) {
                                                    Text("View", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Violet600)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { navController.navigate(Routes.REPORT_TYPE) },
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Gray200)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.Add, contentDescription = null, tint = Gray400, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Create New Report", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray400)
                            }
                        }
                    }
                    1 -> { // Claims
                        if (myClaims.isEmpty()) {
                            EmptyState("No Claims Yet", "Items you've claimed will appear here")
                        } else {
                            myClaims.forEach { claim ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Routes.claimStatus(claim.id)) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        AsyncImage(model = claim.imageUrl, contentDescription = claim.name, modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                Text(claim.name, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900, modifier = Modifier.weight(1f))
                                                StatusBadge(status = ItemStatus.APPROVED, size = BadgeSize.XS)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Claim submitted", fontSize = 12.sp, color = Gray400)
                                            Text(claim.location, fontSize = 12.sp, color = Gray400)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> { // Resolved
                        if (resolvedReports.isEmpty()) {
                            EmptyState("No Resolved Items", "Resolved reports will appear here")
                        } else {
                            resolvedReports.forEach { item ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Routes.itemDetails(item.id)) },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Gray50),
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        AsyncImage(
                                            model = item.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=300&fit=crop&auto=format" },
                                            contentDescription = item.name,
                                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).alpha(0.7f),
                                            contentScale = ContentScale.Crop
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                                Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = OutfitFontFamily, color = Gray500, modifier = Modifier.weight(1f))
                                                StatusBadge(status = item.status, size = BadgeSize.XS)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(item.location, fontSize = 12.sp, color = Gray400)
                                            Text(item.date, fontSize = 12.sp, color = Gray400)
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
}

@Composable
private fun EmptyState(title: String, desc: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(50)).background(Gray100), contentAlignment = Alignment.Center) {
            Text("📋", fontSize = 28.sp, color = Gray300)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray700)
        Spacer(modifier = Modifier.height(8.dp))
        Text(desc, fontSize = 14.sp, color = Gray400)
    }
}
