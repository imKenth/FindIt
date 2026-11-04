package com.findit.app

import com.findit.app.domain.matching.ItemMatcher
import com.findit.app.domain.model.Item
import com.findit.app.domain.model.ItemCategory
import com.findit.app.domain.model.ItemStatus
import com.findit.app.domain.model.ItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemMatcherTest {

    @Test
    fun ranksRelevantOppositeTypeItemsAndExcludesInactiveOrSameTypeItems() {
        val strongMatch = item(
            id = "found-backpack",
            name = "Blue Backpack",
            type = ItemType.FOUND,
            status = ItemStatus.FOUND,
            category = ItemCategory.BAGS,
            location = "Library",
            date = "11/02/2026",
            description = "Blue canvas backpack with a front pocket"
        )
        val claimedItem = strongMatch.copy(id = "claimed", status = ItemStatus.CLAIMED)
        val sameTypeItem = strongMatch.copy(id = "lost", type = ItemType.LOST)

        val matches = ItemMatcher.findMatches(
            name = "Blue Backpack",
            category = ItemCategory.BAGS,
            location = "Library",
            date = "11/03/2026",
            description = "Blue canvas backpack",
            type = ItemType.LOST,
            candidates = listOf(claimedItem, sameTypeItem, strongMatch)
        )

        assertEquals(listOf(strongMatch), matches.map { it.item })
        assertTrue(matches.single().reasons.contains("Same category"))
        assertTrue(matches.single().reasons.contains("Close report dates"))
    }

    @Test
    fun doesNotSuggestItemsWithoutNameOrDescriptionEvidence() {
        val unrelatedItem = item(
            id = "unrelated",
            name = "Phone",
            type = ItemType.FOUND,
            status = ItemStatus.FOUND,
            category = ItemCategory.WALLETS,
            location = "Gym",
            date = "11/02/2026",
            description = "A phone was turned in"
        )

        val matches = ItemMatcher.findMatches(
            name = "Wallet",
            category = ItemCategory.WALLETS,
            location = "Gym",
            date = "11/03/2026",
            description = "Leather card holder",
            type = ItemType.LOST,
            candidates = listOf(unrelatedItem)
        )

        assertTrue(matches.isEmpty())
    }

    private fun item(
        id: String,
        name: String,
        type: ItemType,
        status: ItemStatus,
        category: ItemCategory,
        location: String,
        date: String,
        description: String
    ) = Item(
        id = id,
        name = name,
        type = type,
        status = status,
        category = category,
        location = location,
        date = date,
        description = description
    )
}
