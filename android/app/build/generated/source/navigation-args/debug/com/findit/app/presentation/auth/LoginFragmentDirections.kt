package com.findit.app.presentation.auth

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.findit.app.NavGraphDirections
import com.findit.app.R

public class LoginFragmentDirections private constructor() {
  public companion object {
    public fun actionLoginFragmentToRegisterFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_registerFragment)

    public fun actionLoginFragmentToHomeFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_homeFragment)

    public fun actionLoginFragmentToAdminDashboardFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_loginFragment_to_adminDashboardFragment)

    public fun actionGlobalHome(): NavDirections = NavGraphDirections.actionGlobalHome()

    public fun actionGlobalSearch(): NavDirections = NavGraphDirections.actionGlobalSearch()

    public fun actionGlobalReportType(): NavDirections = NavGraphDirections.actionGlobalReportType()

    public fun actionGlobalMyReports(): NavDirections = NavGraphDirections.actionGlobalMyReports()

    public fun actionGlobalProfile(): NavDirections = NavGraphDirections.actionGlobalProfile()
  }
}
