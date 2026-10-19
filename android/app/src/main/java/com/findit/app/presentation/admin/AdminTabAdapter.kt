package com.findit.app.presentation.admin

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdminTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AdminDashboardTabFragment()
            1 -> AdminReportsTabFragment()
            2 -> AdminClaimsTabFragment()
            3 -> AdminUsersTabFragment()
            else -> AdminDashboardTabFragment()
        }
    }
}
