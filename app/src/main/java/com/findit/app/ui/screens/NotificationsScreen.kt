package com.findit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.findit.app.data.mock.MockData
import com.findit.app.ui.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun NotificationsScreen(navController: NavController) {
    val unread = MockData.notifications.filter { !it.read }
    val read = MockData.notifications.filter { it.read }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) } }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gray600) }
                Text("Notifications", fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900, modifier = Modifier.weight(1f))
                if (unread.isNotEmpty()) {
                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Violet100)) {
                        Text("${unread.size} new", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Violet600)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(White).padding(padding).verticalScroll(rememberScrollState())
        ) {
            if (unread.isNotEmpty()) {
                Text("NEW", modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), fontSize = 10.sp, fontWeight = FontWeight.Black, color = Gray400, letterSpacing = 2.sp)
                unread.forEach { notif ->
                    NotificationItem(
                        title = notif.title,
                        message = notif.message,
                        time = notif.time,
                        type = notif.type,
                        unread = true,
                        onClick = { notif.itemId?.let { navController.navigate(Routes.itemDetails(it)) } },
                    )
                }
            }
            if (read.isNotEmpty()) {
                Text("EARLIER", modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp), fontSize = 10.sp, fontWeight = FontWeight.Black, color = Gray400, letterSpacing = 2.sp)
                read.forEach { notif ->
                    NotificationItem(
                        title = notif.title,
                        message = notif.message,
                        time = notif.time,
                        type = notif.type,
                        unread = false,
                        onClick = { notif.itemId?.let { navController.navigate(Routes.itemDetails(it)) } },
                    )
                }
            }
            if (MockData.notifications.isEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 120.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Gray100), contentAlignment = Alignment.Center) {
                        Text("\uD83D\uDD14", fontSize = 28.sp, color = Gray300)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("All caught up!", fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray700)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No notifications right now", fontSize = 14.sp, color = Gray400)
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    title: String,
    message: String,
    time: String,
    type: String,
    unread: Boolean,
    onClick: () -> Unit,
) {
    data class NotifStyle(val icon: String, val bg: Color, val text: Color)
    val style = when (type) {
        "match" -> NotifStyle("\uD83D\uDCA1", Violet100, Violet600)
        "claim" -> NotifStyle("\uD83C\uDFF7\uFE0F", Amber100, Amber600)
        "approved" -> NotifStyle("\u2714\uFE0F", Green100, Green600)
        "rejected" -> NotifStyle("\u2716", Red100, Red500)
        else -> NotifStyle("\u2139\uFE0F", Blue100, Blue600)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (unread) Violet50.copy(alpha = 0.6f) else White)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(style.bg),
            contentAlignment = Alignment.Center
        ) {
            Text(style.icon, fontSize = 20.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (unread) Gray900 else Gray500, modifier = Modifier.weight(1f))
                Text(time, fontSize = 10.sp, color = Gray400)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(message, fontSize = 12.sp, color = if (unread) Gray600 else Gray400, lineHeight = 18.sp)
        }
        if (unread) {
            Box(modifier = Modifier.padding(top = 4.dp).size(8.dp).clip(CircleShape).background(Violet500))
        }
    }
}
