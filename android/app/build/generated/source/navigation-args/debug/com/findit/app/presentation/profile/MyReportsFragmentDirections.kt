package com.findit.app.presentation.profile

import android.os.Bundle
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.findit.app.NavGraphDirections
import com.findit.app.R
import kotlin.Int
import kotlin.String

public class MyReportsFragmentDirections private constructor() {
  private data class ActionMyReportsFragmentToItemDetailsFragment(
    public val itemId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_myReportsFragment_to_itemDetailsFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("itemId", this.itemId)
        return result
      }
  }

  public companion object {
    public fun actionMyReportsFragmentToItemDetailsFragment(itemId: String): NavDirections =
        ActionMyReportsFragmentToItemDetailsFragment(itemId)

    public fun actionMyReportsFragmentToReportTypeFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_myReportsFragment_to_reportTypeFragment)

    public fun actionMyReportsFragmentToClaimStatusFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_myReportsFragment_to_claimStatusFragment)

    public fun actionGlobalHome(): NavDirections = NavGraphDirections.actionGlobalHome()

    public fun actionGlobalSearch(): NavDirections = NavGraphDirections.actionGlobalSearch()

    public fun actionGlobalReportType(): NavDirections = NavGraphDirections.actionGlobalReportType()

    public fun actionGlobalMyReports(): NavDirections = NavGraphDirections.actionGlobalMyReports()

    public fun actionGlobalProfile(): NavDirections = NavGraphDirections.actionGlobalProfile()
  }
}
