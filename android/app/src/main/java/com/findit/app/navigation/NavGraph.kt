package com.findit.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.findit.app.presentation.auth.*
import com.findit.app.presentation.home.*
import com.findit.app.presentation.search.*
import com.findit.app.presentation.common.*
import com.findit.app.presentation.lost.*
import com.findit.app.presentation.found.*
import com.findit.app.presentation.claims.*
import com.findit.app.presentation.profile.*
import com.findit.app.presentation.notifications.*
import com.findit.app.presentation.admin.*

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val SEARCH = "search"
    const val REPORT_TYPE = "report_type"
    const val REPORT_LOST = "report_lost"
    const val REPORT_FOUND = "report_found"
    const val ITEM_DETAILS = "item_details/{itemId}"
    const val CLAIM_ITEM = "claim_item/{itemId}"
    const val CLAIM_STATUS = "claim_status/{itemId}"
    const val MY_REPORTS = "my_reports"
    const val NOTIFICATIONS = "notifications"
    const val PROFILE = "profile"
    const val ADMIN = "admin"

    fun itemDetails(itemId: String) = "item_details/$itemId"
    fun claimItem(itemId: String) = "claim_item/$itemId"
    fun claimStatus(itemId: String) = "claim_status/$itemId"
}

@Composable
fun FindItNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController = navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }
        composable(Routes.SEARCH) {
            SearchScreen(navController = navController)
        }
        composable(Routes.REPORT_TYPE) {
            ReportTypeScreen(navController = navController)
        }
        composable(Routes.REPORT_LOST) {
            ReportLostScreen(navController = navController)
        }
        composable(Routes.REPORT_FOUND) {
            ReportFoundScreen(navController = navController)
        }
        composable(
            route = Routes.ITEM_DETAILS,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: "1"
            ItemDetailsScreen(navController = navController, itemId = itemId)
        }
        composable(
            route = Routes.CLAIM_ITEM,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: "1"
            ClaimItemScreen(navController = navController, itemId = itemId)
        }
        composable(
            route = Routes.CLAIM_STATUS,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: "5"
            ClaimStatusScreen(navController = navController, itemId = itemId)
        }
        composable(Routes.MY_REPORTS) {
            MyReportsScreen(navController = navController)
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(navController = navController)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(navController = navController)
        }
        composable(Routes.ADMIN) {
            AdminDashboardScreen(navController = navController)
        }
    }
}
