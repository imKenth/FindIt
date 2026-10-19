package com.findit.app.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.findit.app.navigation.Routes
import com.findit.app.ui.theme.*

@Composable
fun BottomNav(
    navController: NavController,
    activeRoute: String,
    notifCount: Int = 0,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = White,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = if (activeRoute == Routes.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                label = "Home",
                isSelected = activeRoute == Routes.HOME,
                onClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
            BottomNavItem(
                icon = if (activeRoute == Routes.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                label = "Search",
                isSelected = activeRoute == Routes.SEARCH,
                onClick = { navController.navigate(Routes.SEARCH) { popUpTo(Routes.HOME) } }
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { navController.navigate(Routes.REPORT_TYPE) }
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Violet600),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Report",
                        tint = White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Reports",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Violet600
                )
            }
            BottomNavItem(
                icon = if (activeRoute == Routes.MY_REPORTS) Icons.Filled.Description else Icons.Outlined.Description,
                label = "My Reports",
                isSelected = activeRoute == Routes.MY_REPORTS,
                onClick = { navController.navigate(Routes.MY_REPORTS) { popUpTo(Routes.HOME) } }
            )
            Box {
                BottomNavItem(
                    icon = if (activeRoute == Routes.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    label = "Profile",
                    isSelected = activeRoute == Routes.PROFILE,
                    onClick = { navController.navigate(Routes.PROFILE) { popUpTo(Routes.HOME) } }
                )
                if (notifCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Red500)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Violet600 else Gray400,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Violet600 else Gray400
        )
    }
}
