package com.findit.app.presentation.claims

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.findit.app.data.remote.MockData
import com.findit.app.domain.model.ItemStatus
import com.findit.app.presentation.common.components.BadgeSize
import com.findit.app.presentation.common.components.StatusBadge
import com.findit.app.navigation.Routes
import com.findit.app.ui.theme.*

data class ClaimStep(val label: String, val desc: String, val done: Boolean, val active: Boolean = false)

@Composable
fun ClaimStatusScreen(navController: NavController, itemId: String) {
    val item = MockData.items.find { it.id == itemId } ?: MockData.items.first()
    val steps = listOf(
        ClaimStep("Claim Submitted", "Your claim has been received", done = true),
        ClaimStep("Under Review", "An admin is verifying your ownership", done = true, active = true),
        ClaimStep("Decision Made", "Approved or rejected by admin", done = false),
        ClaimStep("Item Collected", "Visit admin office to collect", done = false),
    )

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
                IconButton(onClick = { navController.navigate(Routes.MY_REPORTS) }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gray600) }
                Text("Claim Status", fontSize = 18.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(White).padding(padding).padding(horizontal = 20.dp, vertical = 24.dp).verticalScroll(rememberScrollState())
        ) {
            // Status hero
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Amber50)) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    StatusBadge(status = ItemStatus.PENDING, size = BadgeSize.MD)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(item.name, fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Your claim is currently under review by an administrator. You'll be notified of the decision soon.", fontSize = 14.sp, color = Amber700, lineHeight = 20.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Submitted \u00B7 Aug 28, 2026", fontSize = 12.sp, color = Gray400, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progress steps
            Text("Claim Progress", fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = OutfitFontFamily, color = Gray900)
            Spacer(modifier = Modifier.height(16.dp))

            steps.forEachIndexed { i, step ->
                Row {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        step.done && !step.active -> Green500
                                        step.active -> Amber400
                                        else -> Gray200
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (step.done && !step.active) {
                                Text("\u2714", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            } else if (step.active) {
                                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(White))
                            } else {
                                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Gray400))
                            }
                        }
                        if (i < (steps.size - 1)) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(32.dp)
                                    .background(if (step.done && !step.active) Green100 else Gray200)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.padding(bottom = 32.dp)) {
                        Text(step.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (step.done || step.active) Gray900 else Gray400)
                        Text(step.desc, fontSize = 12.sp, color = Gray500)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = { navController.navigate(Routes.HOME) { popUpTo(0) } }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
                Text("Back to Home", fontWeight = FontWeight.Bold)
            }
        }
    }
}
