package com.findit.app

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections

public class NavGraphDirections private constructor() {
  public companion object {
    public fun actionGlobalHome(): NavDirections = ActionOnlyNavDirections(R.id.action_global_home)

    public fun actionGlobalSearch(): NavDirections =
        ActionOnlyNavDirections(R.id.action_global_search)

    public fun actionGlobalReportType(): NavDirections =
        ActionOnlyNavDirections(R.id.action_global_report_type)

    public fun actionGlobalMyReports(): NavDirections =
        ActionOnlyNavDirections(R.id.action_global_my_reports)

    public fun actionGlobalProfile(): NavDirections =
        ActionOnlyNavDirections(R.id.action_global_profile)
  }
}
