package com.findit.app.presentation.common

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.findit.app.NavGraphDirections
import com.findit.app.R

public class ReportTypeFragmentDirections private constructor() {
  public companion object {
    public fun actionReportTypeFragmentToReportLostFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_reportTypeFragment_to_reportLostFragment)

    public fun actionReportTypeFragmentToReportFoundFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_reportTypeFragment_to_reportFoundFragment)

    public fun actionGlobalHome(): NavDirections = NavGraphDirections.actionGlobalHome()

    public fun actionGlobalSearch(): NavDirections = NavGraphDirections.actionGlobalSearch()

    public fun actionGlobalReportType(): NavDirections = NavGraphDirections.actionGlobalReportType()

    public fun actionGlobalMyReports(): NavDirections = NavGraphDirections.actionGlobalMyReports()

    public fun actionGlobalProfile(): NavDirections = NavGraphDirections.actionGlobalProfile()
  }
}
