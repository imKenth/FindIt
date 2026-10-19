package com.findit.app.presentation.home

import android.os.Bundle
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.findit.app.NavGraphDirections
import com.findit.app.R
import kotlin.Int
import kotlin.String

public class HomeFragmentDirections private constructor() {
  private data class ActionHomeFragmentToItemDetailsFragment(
    public val itemId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_homeFragment_to_itemDetailsFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("itemId", this.itemId)
        return result
      }
  }

  public companion object {
    public fun actionHomeFragmentToReportTypeFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_reportTypeFragment)

    public fun actionHomeFragmentToItemDetailsFragment(itemId: String): NavDirections =
        ActionHomeFragmentToItemDetailsFragment(itemId)

    public fun actionHomeFragmentToSearchFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_searchFragment)

    public fun actionHomeFragmentToNotificationsFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_notificationsFragment)

    public fun actionHomeFragmentToProfileFragment(): NavDirections =
        ActionOnlyNavDirections(R.id.action_homeFragment_to_profileFragment)

    public fun actionGlobalHome(): NavDirections = NavGraphDirections.actionGlobalHome()

    public fun actionGlobalSearch(): NavDirections = NavGraphDirections.actionGlobalSearch()

    public fun actionGlobalReportType(): NavDirections = NavGraphDirections.actionGlobalReportType()

    public fun actionGlobalMyReports(): NavDirections = NavGraphDirections.actionGlobalMyReports()

    public fun actionGlobalProfile(): NavDirections = NavGraphDirections.actionGlobalProfile()
  }
}
