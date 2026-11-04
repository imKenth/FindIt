package com.findit.app.domain.matching

import com.findit.app.domain.model.Item
import com.findit.app.domain.model.ItemCategory
import com.findit.app.domain.model.ItemStatus
import com.findit.app.domain.model.ItemType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

data class ItemMatch(
    val item: Item,
    val score: Int,
    val reasons: List<String>
)

object ItemMatcher {
    private const val MINIMUM_SCORE = 35
    private const val MAX_RESULTS = 5
    private val ignoredWords = setOf("a", "an", "and", "for", "found", "item", "lost", "my", "of", "the", "with")
    private val dateFormats = listOf(
        DateTimeFormatter.ofPattern("M/d/uuuu", Locale.ROOT),
        DateTimeFormatter.ISO_LOCAL_DATE
    )

    fun findMatches(
        name: String,
        category: ItemCategory,
        location: String,
        date: String,
        description: String,
        type: ItemType,
        candidates: List<Item>
    ): List<ItemMatch> {
        val nameTokens = tokens(name)
        if (nameTokens.isEmpty()) return emptyList()

        return candidates.asSequence()
            .filter { it.type != type && it.status in setOf(ItemStatus.LOST, ItemStatus.FOUND) }
            .mapNotNull { item -> score(item, nameTokens, category, location, date, description) }
            .filter { it.score >= MINIMUM_SCORE }
            .sortedByDescending { it.score }
            .take(MAX_RESULTS)
            .toList()
    }

    private fun score(
        item: Item,
        nameTokens: Set<String>,
        category: ItemCategory,
        location: String,
        date: String,
        description: String
    ): ItemMatch? {
        val candidateNameTokens = tokens(item.name)
        val nameSimilarity = similarity(nameTokens, candidateNameTokens)
        val descriptionSimilarity = similarity(tokens(description), tokens(item.description))
        if (nameSimilarity < 0.25 && descriptionSimilarity < 0.2) return null

        val reasons = mutableListOf<String>()
        var score = 0.0
        if (nameSimilarity > 0) {
            score += 45 * nameSimilarity
            reasons += "Similar item name"
        }
        if (descriptionSimilarity >= 0.2) {
            score += 20 * descriptionSimilarity
            reasons += "Description details overlap"
        }
        if (category != ItemCategory.OTHER && category == item.category) {
            score += 20
            reasons += "Same category"
        }

        val locationSimilarity = similarity(tokens(location), tokens(item.location))
        if (locationSimilarity > 0) {
            score += 10 * locationSimilarity
            reasons += "Related location"
        }

        val daysApart = daysApart(date, item.date)
        if (daysApart != null && daysApart <= 7) {
            score += if (daysApart <= 3) 5.0 else 2.5
            reasons += "Close report dates"
        }

        return ItemMatch(item, score.toInt(), reasons)
    }

    private fun tokens(value: String): Set<String> =
        value.lowercase(Locale.ROOT)
            .split(Regex("[^\\p{L}\\p{N}]+"))
            .filter { it.length > 1 && it !in ignoredWords }
            .toSet()

    private fun similarity(first: Set<String>, second: Set<String>): Double {
        if (first.isEmpty() || second.isEmpty()) return 0.0
        val intersection = first.intersect(second).size
        return intersection.toDouble() / (first.size + second.size - intersection)
    }

    private fun daysApart(first: String, second: String): Long? {
        val firstDate = parseDate(first) ?: return null
        val secondDate = parseDate(second) ?: return null
        return kotlin.math.abs(java.time.temporal.ChronoUnit.DAYS.between(firstDate, secondDate))
    }

    private fun parseDate(value: String): LocalDate? =
        dateFormats.firstNotNullOfOrNull { formatter ->
            try {
                LocalDate.parse(value.trim(), formatter)
            } catch (_: DateTimeParseException) {
                null
            }
        }
}
