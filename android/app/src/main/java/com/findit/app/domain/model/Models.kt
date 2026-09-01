package com.findit.app.domain.model

import kotlinx.serialization.Serializable

enum class ItemStatus { LOST, FOUND, CLAIMED, RESOLVED, PENDING, APPROVED, REJECTED }
enum class ItemCategory { ELECTRONICS, IDS, BAGS, WALLETS, KEYS, BOOKS, CLOTHING, OTHER }
enum class ItemType { LOST, FOUND }

@Serializable
data class Item(
    val id: String,
    val name: String,
    val category: ItemCategory,
    val status: ItemStatus,
    val location: String,
    val date: String,
    val time: String? = null,
    val description: String,
    val imageUrl: String,
    val reportedBy: String,
    val type: ItemType
)

@Serializable
data class AppNotification(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val time: String,
    val read: Boolean,
    val itemId: String? = null
)

@Serializable
data class Claim(
    val id: String,
    val itemId: String,
    val claimant: String,
    val reason: String,
    val lostWhen: String,
    val privateDetail: String,
    val status: ItemStatus,
    val submittedDate: String
)

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatar: String,
    val reportsCount: Int,
    val claimsCount: Int,
    val resolvedCount: Int
)

enum class Screen {
    SPLASH, LOGIN, REGISTER, HOME, SEARCH,
    REPORT_TYPE, REPORT_LOST, REPORT_FOUND,
    ITEM_DETAILS, CLAIM_ITEM, CLAIM_STATUS,
    MY_REPORTS, NOTIFICATIONS, PROFILE, ADMIN
}
