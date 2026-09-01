package com.findit.app.presentation.claims

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.findit.app.data.remote.MockData
import com.findit.app.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun ClaimItemScreen(navController: NavController, itemId: String) {
    val item = MockData.items.find { it.id == itemId } ?: MockData.items.first()
    var reason by remember { mutableStateOf("") }
    var lostWhen by remember { mutableStateOf("") }
    var privateDetail by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(onClick = { navController.navigate(Routes.itemDetails(item.id)) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gray600) }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Claim Item", fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                    Text(item.name, fontSize = 12.sp, color = Gray500, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(White).padding(padding).padding(horizontal = 20.dp, vertical = 20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Item preview
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Gray50)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AsyncImage(model = item.imageUrl, contentDescription = item.name, modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                    Column {
                        Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                        Text("${item.category.name.replace("_", " ")} \u00B7 ${item.location}", fontSize = 12.sp, color = Gray500)
                    }
                }
            }

            // Privacy notice
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Green50)) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("\uD83D\uDD12", fontSize = 20.sp)
                    Column {
                        Text("Your claim is private", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Green800)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Everything you submit here is visible only to administrators. It will not be shown publicly and is used solely for ownership verification.", fontSize = 12.sp, color = Green700, lineHeight = 18.sp)
                    }
                }
            }

            Column {
                Text("Why do you believe this is yours? *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray700)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = reason, onValueChange = { reason = it }, placeholder = { Text("Describe why you believe this item belongs to you...", color = Gray400) }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), maxLines = 4)
            }

            Column {
                Text("Where and when did you lose it? *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray700)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = lostWhen, onValueChange = { lostWhen = it }, placeholder = { Text("e.g. \"I lost it on Aug 27 in the Library around 2:30 PM...\"", color = Gray400) }, modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), maxLines = 4)
            }

            Column {
                Text("Private Identifying Detail *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray700)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = privateDetail, onValueChange = { privateDetail = it }, placeholder = { Text("e.g. \"Lock screen is a sunset photo, has a crack top-right\"", color = Gray400) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Gray200, focusedBorderColor = Violet400, unfocusedContainerColor = Gray50, focusedContainerColor = Gray50), singleLine = true)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Provide a detail only the true owner would know. This is kept strictly private.", fontSize = 12.sp, color = Gray400, lineHeight = 18.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.navigate(Routes.claimStatus(item.id)) }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Violet600), elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)) {
                Text("Submit Claim", fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily)
            }
        }
    }
}
