package com.findit.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.FirebaseApp
import com.findit.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
                ?: return
        val navController = navHostFragment.navController
        configureBottomNavigation(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigation()?.visibility =
                if (destination.id in bottomNavigationDestinations) View.VISIBLE else View.GONE
            updateSelectedItem(destination.id)
        }
        navController.currentDestination?.id?.let { destinationId ->
            bottomNavigation()?.visibility =
                if (destinationId in bottomNavigationDestinations) View.VISIBLE else View.GONE
            updateSelectedItem(destinationId)
        }
    }

    private fun configureBottomNavigation(navController: NavController) {
        val root = bottomNavigation() ?: return
        configureItem(root, R.id.nav_home, R.drawable.ic_home, R.string.nav_home) {
            navController.navigate(R.id.action_global_home)
        }
        configureItem(root, R.id.nav_search, R.drawable.ic_search_nav, R.string.nav_search) {
            navController.navigate(R.id.action_global_search)
        }
        root.findViewById<View>(R.id.nav_reports).setOnClickListener {
            navController.navigate(R.id.action_global_report_type)
        }
        configureItem(root, R.id.nav_my_reports, R.drawable.ic_document, R.string.nav_my_reports) {
            navController.navigate(R.id.action_global_my_reports)
        }
        configureItem(root, R.id.nav_profile, R.drawable.ic_person, R.string.nav_profile) {
            navController.navigate(R.id.action_global_profile)
        }
    }

    private fun configureItem(
        root: View,
        itemId: Int,
        iconRes: Int,
        labelRes: Int,
        onClick: () -> Unit
    ) {
        val item = root.findViewById<View>(itemId) ?: return
        item.findViewById<android.widget.ImageView>(R.id.nav_item_icon).setImageResource(iconRes)
        item.findViewById<android.widget.TextView>(R.id.nav_item_label).setText(labelRes)
        item.setOnClickListener { onClick() }
    }

    private fun updateSelectedItem(destinationId: Int) {
        val selectedItemId = when (destinationId) {
            R.id.homeFragment -> R.id.nav_home
            R.id.searchFragment -> R.id.nav_search
            R.id.myReportsFragment -> R.id.nav_my_reports
            R.id.profileFragment -> R.id.nav_profile
            else -> null
        }
        listOf(R.id.nav_home, R.id.nav_search, R.id.nav_my_reports, R.id.nav_profile).forEach { itemId ->
            val item = bottomNavigation()?.findViewById<View>(itemId) ?: return@forEach
            val color = ContextCompat.getColor(
                this,
                if (itemId == selectedItemId) R.color.bottom_nav_selected
                else R.color.bottom_nav_unselected
            )
            item.findViewById<android.widget.ImageView>(R.id.nav_item_icon).setColorFilter(color)
            item.findViewById<android.widget.TextView>(R.id.nav_item_label).setTextColor(color)
        }
    }

    private fun bottomNavigation(): View? =
        binding.root.findViewById(R.id.bottom_navigation)

    private val bottomNavigationDestinations = setOf(
        R.id.homeFragment,
        R.id.searchFragment,
        R.id.reportTypeFragment,
        R.id.myReportsFragment,
        R.id.profileFragment
    )
}
