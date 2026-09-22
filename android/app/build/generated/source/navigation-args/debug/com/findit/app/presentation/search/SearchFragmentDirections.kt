package com.findit.app.presentation.search

import android.os.Bundle
import androidx.navigation.NavDirections
import com.findit.app.NavGraphDirections
import com.findit.app.R
import kotlin.Int
import kotlin.String

public class SearchFragmentDirections private constructor() {
  private data class ActionSearchFragmentToItemDetailsFragment(
    public val itemId: String,
  ) : NavDirections {
    public override val actionId: Int = R.id.action_searchFragment_to_itemDetailsFragment

    public override val arguments: Bundle
      get() {
        val result = Bundle()
        result.putString("itemId", this.itemId)
        return result
      }
  }

  public companion object {
    public fun actionSearchFragmentToItemDetailsFragment(itemId: String): NavDirections =
        ActionSearchFragmentToItemDetailsFragment(itemId)

    public fun actionGlobalHome(): NavDirections = NavGraphDirections.actionGlobalHome()

    public fun actionGlobalSearch(): NavDirections = NavGraphDirections.actionGlobalSearch()

    public fun actionGlobalReportType(): NavDirections = NavGraphDirections.actionGlobalReportType()

    public fun actionGlobalMyReports(): NavDirections = NavGraphDirections.actionGlobalMyReports()

    public fun actionGlobalProfile(): NavDirections = NavGraphDirections.actionGlobalProfile()
  }
}
