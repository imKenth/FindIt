package com.findit.app.presentation.admin

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.findit.app.NavGraphDirections
import com.findit.app.R

public class AdminDashboardFragmentDirections private constructor() {
  public companion object {
    public fun actionAdminDashboardFragmentToLoginFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_adminDashboardFragment_to_loginFragment)

    public fun actionGlobalHome(): NavDirections = NavGraphDirections.actionGlobalHome()

    public fun actionGlobalSearch(): NavDirections = NavGraphDirections.actionGlobalSearch()

    public fun actionGlobalReportType(): NavDirections = NavGraphDirections.actionGlobalReportType()

    public fun actionGlobalMyReports(): NavDirections = NavGraphDirections.actionGlobalMyReports()

    public fun actionGlobalProfile(): NavDirections = NavGraphDirections.actionGlobalProfile()
  }
}
