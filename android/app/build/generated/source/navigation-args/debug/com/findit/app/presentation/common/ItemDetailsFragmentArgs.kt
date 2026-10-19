package com.findit.app.presentation.common

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException
import kotlin.String
import kotlin.jvm.JvmStatic

public data class ItemDetailsFragmentArgs(
  public val itemId: String,
) : NavArgs {
  public fun toBundle(): Bundle {
    val result = Bundle()
    result.putString("itemId", this.itemId)
    return result
  }

  public fun toSavedStateHandle(): SavedStateHandle {
    val result = SavedStateHandle()
    result.set("itemId", this.itemId)
    return result
  }

  public companion object {
    @JvmStatic
    public fun fromBundle(bundle: Bundle): ItemDetailsFragmentArgs {
      bundle.setClassLoader(ItemDetailsFragmentArgs::class.java.classLoader)
      val __itemId : String?
      if (bundle.containsKey("itemId")) {
        __itemId = bundle.getString("itemId")
        if (__itemId == null) {
          throw IllegalArgumentException("Argument \"itemId\" is marked as non-null but was passed a null value.")
        }
      } else {
        throw IllegalArgumentException("Required argument \"itemId\" is missing and does not have an android:defaultValue")
      }
      return ItemDetailsFragmentArgs(__itemId)
    }

    @JvmStatic
    public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): ItemDetailsFragmentArgs {
      val __itemId : String?
      if (savedStateHandle.contains("itemId")) {
        __itemId = savedStateHandle["itemId"]
        if (__itemId == null) {
          throw IllegalArgumentException("Argument \"itemId\" is marked as non-null but was passed a null value")
        }
      } else {
        throw IllegalArgumentException("Required argument \"itemId\" is missing and does not have an android:defaultValue")
      }
      return ItemDetailsFragmentArgs(__itemId)
    }
  }
}
