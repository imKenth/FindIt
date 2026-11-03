package com.findit.app.presentation.report

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findit.app.domain.model.Item
import com.findit.app.data.repository.ItemRepository
import com.findit.app.domain.matching.ItemMatch
import com.findit.app.domain.matching.ItemMatcher
import com.findit.app.domain.model.ItemStatus
import com.findit.app.domain.model.ItemType
import com.findit.app.domain.model.ItemCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ReportViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState = _reportState.asStateFlow()
    private val matchQuery = MutableStateFlow(MatchQuery())
    val matchingItems = combine(repository.getAllItems(), matchQuery) { items, query ->
        ItemMatcher.findMatches(
            name = query.name,
            category = query.category,
            location = query.location,
            date = query.date,
            description = query.description,
            type = query.type,
            candidates = items
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateMatchQuery(
        name: String,
        category: String,
        location: String,
        date: String,
        description: String,
        type: ItemType
    ) {
        matchQuery.value = MatchQuery(
            name = name,
            category = ItemCategory.entries.firstOrNull {
                it.name.equals(category.replace(" ", "_"), ignoreCase = true)
            } ?: ItemCategory.OTHER,
            location = location,
            date = date,
            description = description,
            type = type
        )
    }

    fun reportItem(
        name: String,
        category: String,
        location: String,
        date: String,
        time: String?,
        description: String,
        type: ItemType,
        reportedBy: String,
        reportedById: String,
        imageUri: Uri? = null
    ) {
        viewModelScope.launch {
            _reportState.value = ReportState.Loading
            try {
                val itemId = UUID.randomUUID().toString()
                val imageUrl = imageUri?.let { repository.uploadItemImage(itemId, it) }.orEmpty()
                val item = Item(
                    id = itemId,
                    name = name,
                    category = try { ItemCategory.valueOf(category.uppercase().replace(" ", "_")) } catch (e: Exception) { ItemCategory.OTHER },
                    status = if (type == ItemType.LOST) ItemStatus.LOST else ItemStatus.FOUND,
                    location = location,
                    date = date,
                    time = time,
                    description = description,
                    imageUrl = imageUrl,
                    reportedBy = reportedBy,
                    reportedById = reportedById,
                    type = type
                )
                repository.reportItem(item)
                _reportState.value = ReportState.Success
            } catch (e: Exception) {
                _reportState.value = ReportState.Error(e.message ?: "Failed to submit report")
            }
        }
    }

    fun resetState() {
        _reportState.value = ReportState.Idle
    }

    private data class MatchQuery(
        val name: String = "",
        val category: ItemCategory = ItemCategory.OTHER,
        val location: String = "",
        val date: String = "",
        val description: String = "",
        val type: ItemType = ItemType.LOST
    )
}

sealed class ReportState {
    object Idle : ReportState()
    object Loading : ReportState()
    object Success : ReportState()
    data class Error(val message: String) : ReportState()
}
