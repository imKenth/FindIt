package com.findit.app.presentation.profile

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.findit.app.NavGraphDirections
import com.findit.app.R

public class ProfileFragmentDirections private constructor() {
  public companion object {
    public fun actionProfileFragmentToLoginFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profileFragment_to_loginFragment)

    public fun actionProfileFragmentToAdminDashboardFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profileFragment_to_adminDashboardFragment)

    public fun actionProfileFragmentToMyReportsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profileFragment_to_myReportsFragment)

    public fun actionProfileFragmentToNotificationsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_profileFragment_to_notificationsFragment)

    public fun actionGlobalHome(): NavDirections = NavGraphDirections.actionGlobalHome()

    public fun actionGlobalSearch(): NavDirections = NavGraphDirections.actionGlobalSearch()

    public fun actionGlobalReportType(): NavDirections = NavGraphDirections.actionGlobalReportType()

    public fun actionGlobalMyReports(): NavDirections = NavGraphDirections.actionGlobalMyReports()

    public fun actionGlobalProfile(): NavDirections = NavGraphDirections.actionGlobalProfile()
  }
}
