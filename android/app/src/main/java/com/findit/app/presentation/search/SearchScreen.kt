package com.findit.app.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.findit.app.data.remote.MockData
import com.findit.app.domain.model.ItemCategory
import com.findit.app.presentation.common.components.BottomNav
import com.findit.app.presentation.common.components.ItemCard
import com.findit.app.navigation.Routes
import com.findit.app.presentation.home.HomeViewModel
import com.findit.app.ui.theme.*

@Composable
fun SearchScreen(navController: NavController) {
    val viewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(com.findit.app.di.AppModule.itemRepository) as T
            }
        }
    )

    val liveItems by viewModel.items.collectAsState()
    val allItems = if (liveItems.isNotEmpty()) liveItems else MockData.items

    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All") + ItemCategory.entries.dropLast(1).map { it.name.replace("_", " ") }

    val results = allItems.filter { item ->
        val q = query.lowercase()
        val matchQ = q.isEmpty() || item.name.lowercase().contains(q) || item.location.lowercase().contains(q) || item.category.name.lowercase().contains(q) || item.description.lowercase().contains(q)
        val matchF = (filter == "All") || (item.type.name == filter.uppercase())
        val matchC = selectedCategory == "All" || item.category.name.replace("_", " ") == selectedCategory
        matchQ && matchF && matchC
    }

    Scaffold(
        bottomBar = { BottomNav(navController = navController, activeRoute = Routes.SEARCH) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .consumeWindowInsets(padding)
        ) {
            // Header with search
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search items, locations, categories…", color = Gray400) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Gray400) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Filled.Close, contentDescription = null, tint = Gray400)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Gray200,
                        focusedBorderColor = Violet400,
                        unfocusedContainerColor = Gray50,
                        focusedContainerColor = Gray50
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Filter chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Lost", "Found").forEach { f ->
                        FilterChip(
                            selected = filter == f,
                            onClick = { filter = f },
                            label = { Text(f, fontWeight = FontWeight.Bold) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Violet600,
                                selectedLabelColor = White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Category chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Violet100,
                                selectedLabelColor = Violet700
                            )
                        )
                    }
                }
            }

            HorizontalDivider(color = Gray100)

            // Results
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                if (results.isNotEmpty()) {
                    Text(
                        text = "${results.size} result${if (results.size != 1) "s" else ""} found",
                        fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gray400
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    results.forEach { item ->
                        ItemCard(
                            item = item,
                            onClick = { navController.navigate(Routes.itemDetails(item.id)) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(50)).background(Gray100),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Search, contentDescription = null, tint = Gray300, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No Results Found", fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray700)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Try different keywords or clear your filters", fontSize = 14.sp, color = Gray400)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { query = ""; filter = "All"; selectedCategory = "All" },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Violet600)
                        ) {
                            Text("Clear Filters", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
