package com.findit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.findit.app.data.mock.MockData
import com.findit.app.data.model.ItemStatus
import com.findit.app.ui.components.StatusBadge
import com.findit.app.ui.components.BadgeSize
import com.findit.app.ui.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun ItemDetailsScreen(navController: NavController, itemId: String) {
    val item = MockData.items.find { it.id == itemId } ?: MockData.items.first()
    val canClaim = (item.status == ItemStatus.LOST) || (item.status == ItemStatus.FOUND)

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Hero image
            Box(modifier = Modifier.fillMaxWidth().height(208.dp)) {
                AsyncImage(model = item.imageUrl, contentDescription = item.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))))

                // Back button
                IconButton(
                    onClick = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) } },
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.3f)),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = White)
                }

                // Share
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = "Share", tint = White)
                }

                // Status + category
                Row(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(status = item.status, size = BadgeSize.MD)
                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = White.copy(alpha = 0.9f))) {
                        Text(item.category.name.replace("_", " "), modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Gray800)
                    }
                }
            }

            // Content
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(item.name, fontSize = 22.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900, lineHeight = 28.sp)
                Spacer(modifier = Modifier.height(20.dp))

                MetaRow("\uD83D\uDCCD", "Location", item.location)
                Spacer(modifier = Modifier.height(10.dp))
                MetaRow("\uD83D\uDCC5", "Date Reported", "${item.date}${item.time?.let { " \u00B7 $it" } ?: ""}")
                Spacer(modifier = Modifier.height(10.dp))
                MetaRow("\uD83D\uDC64", "Reported By", item.reportedBy)

                Spacer(modifier = Modifier.height(20.dp))
                Text("Description", fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.description, fontSize = 14.sp, color = Gray600, lineHeight = 22.sp)

                Spacer(modifier = Modifier.height(20.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Blue50)) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("\uD83D\uDD12", fontSize = 16.sp)
                        Text("Ownership information is kept private. Claim details are only visible to school administrators for verification.", fontSize = 12.sp, color = Blue700, lineHeight = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Bottom action buttons
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                .background(White)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (canClaim) {
                Button(
                    onClick = { navController.navigate(Routes.claimItem(item.id)) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Violet600),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text("Claim This Item", fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily)
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Gray100)) {
                    Text("Item is ${item.status.name} \u2014 not available to claim", modifier = Modifier.fillMaxWidth().padding(16.dp), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray400, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(16.dp)) {
                Text("Report Inappropriate Content", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray500)
            }
        }
    }
}

@Composable
private fun MetaRow(emoji: String, label: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Gray50)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Violet100), contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 16.sp)
            }
            Column {
                Text(label, fontSize = 10.sp, color = Gray400, fontWeight = FontWeight.Bold)
                Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Gray900, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
        }
    }
}
