package com.findit.app.presentation.lost

import androidx.navigation.NavDirections
import com.findit.app.NavGraphDirections

public class ReportLostFragmentDirections private constructor() {
  public companion object {
    public fun actionGlobalHome(): NavDirections = NavGraphDirections.actionGlobalHome()

    public fun actionGlobalSearch(): NavDirections = NavGraphDirections.actionGlobalSearch()

    public fun actionGlobalReportType(): NavDirections = NavGraphDirections.actionGlobalReportType()

    public fun actionGlobalMyReports(): NavDirections = NavGraphDirections.actionGlobalMyReports()

    public fun actionGlobalProfile(): NavDirections = NavGraphDirections.actionGlobalProfile()
  }
}
