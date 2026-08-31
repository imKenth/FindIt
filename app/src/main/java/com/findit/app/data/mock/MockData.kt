package com.findit.app.data.mock

import com.findit.app.data.model.*

object MockData {

    val items = listOf(
        Item(
            id = "1",
            name = "iPhone 14 Pro",
            category = ItemCategory.ELECTRONICS,
            status = ItemStatus.LOST,
            location = "Library, 2nd Floor",
            date = "Aug 27, 2026",
            time = "2:30 PM",
            description = "Space black iPhone 14 Pro with a clear MagSafe case. Has a small crack on the top-right corner of the screen. Lock screen shows a sunset photo over mountains. Lost while studying in the library.",
            imageUrl = "https://images.unsplash.com/photo-1512054502232-10a0a035d672?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Alex Rivera",
            type = ItemType.LOST
        ),
        Item(
            id = "2",
            name = "Blue JanSport Backpack",
            category = ItemCategory.BAGS,
            status = ItemStatus.FOUND,
            location = "Cafeteria, Table 12",
            date = "Aug 27, 2026",
            description = "Navy blue JanSport backpack. Has a small sun keychain attached to the main zipper. Contains textbooks, a green water bottle, and some notebooks. Left unattended after lunch.",
            imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Jordan Chen",
            type = ItemType.FOUND
        ),
        Item(
            id = "3",
            name = "Student ID Card",
            category = ItemCategory.IDS,
            status = ItemStatus.FOUND,
            location = "Main Entrance",
            date = "Aug 26, 2026",
            description = "Found a student ID card near the main entrance glass doors. Submitted to the front office for safekeeping. Contact the admin office to retrieve.",
            imageUrl = "https://images.unsplash.com/photo-1606326608606-aa0b62935f2b?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Sam Parker",
            type = ItemType.FOUND
        ),
        Item(
            id = "4",
            name = "Black Leather Wallet",
            category = ItemCategory.WALLETS,
            status = ItemStatus.LOST,
            location = "Gymnasium",
            date = "Aug 26, 2026",
            time = "4:00 PM",
            description = "Black bifold leather wallet. Contains student ID, bus pass, and some cash. Lost during after-school PE class near the locker rooms. Has a small worn patch on the front.",
            imageUrl = "https://images.unsplash.com/photo-1553409513-37b81cc5d5e3?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Maya Johnson",
            type = ItemType.LOST
        ),
        Item(
            id = "5",
            name = "Sony WH-1000XM5 Headphones",
            category = ItemCategory.ELECTRONICS,
            status = ItemStatus.CLAIMED,
            location = "Study Room B",
            date = "Aug 25, 2026",
            description = "Black Sony noise-cancelling headphones in the original black hard-shell case. Found on a study desk chair in Study Room B on the afternoon of Aug 25.",
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Chris Lee",
            type = ItemType.FOUND
        ),
        Item(
            id = "6",
            name = "House Keys (3 keys)",
            category = ItemCategory.KEYS,
            status = ItemStatus.FOUND,
            location = "Parking Lot A",
            date = "Aug 25, 2026",
            description = "Set of 3 keys on a bright red carabiner keychain. Found near the bicycle racks in Parking Lot A. One key has blue tape marking. Kept at the security booth.",
            imageUrl = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Taylor Brown",
            type = ItemType.FOUND
        ),
        Item(
            id = "7",
            name = "MacBook Air M2",
            category = ItemCategory.ELECTRONICS,
            status = ItemStatus.RESOLVED,
            location = "Computer Lab 3",
            date = "Aug 24, 2026",
            description = "Silver MacBook Air M2 with stickers on the cover including a planet and coffee cup sticker. Lost during the afternoon lab session. Has a dent on the bottom-right corner.",
            imageUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Pat Nguyen",
            type = ItemType.LOST
        ),
        Item(
            id = "8",
            name = "Calculus Textbook",
            category = ItemCategory.BOOKS,
            status = ItemStatus.FOUND,
            location = "Room 204",
            date = "Aug 24, 2026",
            description = "Calculus: Early Transcendentals, 9th Edition by Stewart. Name and student number written inside the front cover in blue ink. Left after 3rd period math class.",
            imageUrl = "https://images.unsplash.com/photo-1524578271613-d550eacf6090?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Morgan Davis",
            type = ItemType.FOUND
        )
    )

    val myReports = listOf(
        Item(
            id = "1",
            name = "iPhone 14 Pro",
            category = ItemCategory.ELECTRONICS,
            status = ItemStatus.LOST,
            location = "Library, 2nd Floor",
            date = "Aug 27, 2026",
            time = "2:30 PM",
            description = "Space black iPhone 14 Pro with a clear MagSafe case.",
            imageUrl = "https://images.unsplash.com/photo-1512054502232-10a0a035d672?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Alex Rivera",
            type = ItemType.LOST
        ),
        Item(
            id = "9",
            name = "Purple Hydro Flask",
            category = ItemCategory.OTHER,
            status = ItemStatus.FOUND,
            location = "Track Field",
            date = "Aug 25, 2026",
            description = "Purple 32oz Hydro Flask with stickers. Found near the bleachers.",
            imageUrl = "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Alex Rivera",
            type = ItemType.FOUND
        )
    )

    val myClaims = listOf(
        Item(
            id = "5",
            name = "Sony WH-1000XM5 Headphones",
            category = ItemCategory.ELECTRONICS,
            status = ItemStatus.CLAIMED,
            location = "Study Room B",
            date = "Aug 25, 2026",
            description = "Black Sony noise-cancelling headphones in their original case.",
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Chris Lee",
            type = ItemType.FOUND
        )
    )

    val resolvedReports = listOf(
        Item(
            id = "7",
            name = "MacBook Air M2",
            category = ItemCategory.ELECTRONICS,
            status = ItemStatus.RESOLVED,
            location = "Computer Lab 3",
            date = "Aug 24, 2026",
            description = "Silver MacBook Air M2. Resolved after matching owner.",
            imageUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=300&fit=crop&auto=format",
            reportedBy = "Alex Rivera",
            type = ItemType.LOST
        )
    )

    val notifications = listOf(
        AppNotification(
            id = "1",
            type = "match",
            title = "Possible Match Found!",
            message = "Your lost iPhone 14 Pro may match a found item reported near the Library. Check it out.",
            time = "2 min ago",
            read = false,
            itemId = "1"
        ),
        AppNotification(
            id = "2",
            type = "claim",
            title = "New Claim Request",
            message = "Someone submitted a claim for the Blue JanSport Backpack you reported found. Review the claim.",
            time = "1 hr ago",
            read = false,
            itemId = "2"
        ),
        AppNotification(
            id = "3",
            type = "approved",
            title = "Claim Approved!",
            message = "Your claim for Sony WH-1000XM5 Headphones has been approved. Visit the admin office to collect.",
            time = "3 hrs ago",
            read = true,
            itemId = "5"
        ),
        AppNotification(
            id = "4",
            type = "update",
            title = "Report Resolved",
            message = "Your lost MacBook Air M2 report has been marked as Resolved. Great news!",
            time = "Yesterday",
            read = true,
            itemId = "7"
        ),
        AppNotification(
            id = "5",
            type = "match",
            title = "Possible Match Found!",
            message = "Your lost wallet may match a found item reported in the Gymnasium area.",
            time = "Yesterday",
            read = true,
            itemId = "4"
        )
    )

    val currentUser = User(
        id = "u1",
        name = "Alex Rivera",
        email = "alex.rivera@school.edu",
        avatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&h=200&fit=crop&auto=format",
        reportsCount = 3,
        claimsCount = 1,
        resolvedCount = 2
    )

    val adminClaims = listOf(
        mapOf("id" to "c1", "item" to "Blue JanSport Backpack", "claimant" to "Jordan Lee", "when" to "Aug 27", "detail" to "Has a sun keychain, contains chemistry book and green water bottle"),
        mapOf("id" to "c2", "item" to "Student ID Card", "claimant" to "Riley Smith", "when" to "Aug 26", "detail" to "Student ID #20241088, issued Feb 2024"),
        mapOf("id" to "c3", "item" to "House Keys (3 keys)", "claimant" to "Casey Brown", "when" to "Aug 26", "detail" to "One key has blue tape, REI carabiner")
    )

    val adminUsers = listOf(
        mapOf("id" to "u1", "name" to "Alex Rivera", "email" to "alex.r@school.edu", "reports" to 3, "status" to "Active"),
        mapOf("id" to "u2", "name" to "Jordan Chen", "email" to "j.chen@school.edu", "reports" to 5, "status" to "Active"),
        mapOf("id" to "u3", "name" to "Sam Parker", "email" to "s.parker@school.edu", "reports" to 1, "status" to "Active"),
        mapOf("id" to "u4", "name" to "Unknown User", "email" to "anon1045@school.edu", "reports" to 0, "status" to "Suspended"),
        mapOf("id" to "u5", "name" to "Taylor Brown", "email" to "t.brown@school.edu", "reports" to 2, "status" to "Active")
    )

    val adminActivity = listOf(
        mapOf("action" to "Claim approved", "item" to "Sony WH-1000XM5", "user" to "Alex Rivera", "time" to "5m ago", "type" to "approved"),
        mapOf("action" to "New report", "item" to "iPhone 14 Pro", "user" to "Alex Rivera", "time" to "12m ago", "type" to "report"),
        mapOf("action" to "Content flagged", "item" to "Brown Leather Jacket", "user" to "System", "time" to "1h ago", "type" to "flag"),
        mapOf("action" to "Claim rejected", "item" to "MacBook Air M2", "user" to "Sam Parker", "time" to "2h ago", "type" to "rejected"),
        mapOf("action" to "User suspended", "item" to "Account #1045", "user" to "Admin", "time" to "3h ago", "type" to "suspend")
    )
}
