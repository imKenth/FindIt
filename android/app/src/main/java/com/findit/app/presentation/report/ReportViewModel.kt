package com.findit.app.presentation.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findit.app.domain.model.Item
import com.findit.app.data.repository.ItemRepository
import com.findit.app.domain.model.ItemStatus
import com.findit.app.domain.model.ItemType
import com.findit.app.domain.model.ItemCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ReportViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState = _reportState.asStateFlow()

    fun reportItem(
        name: String,
        category: String,
        location: String,
        date: String,
        time: String?,
        description: String,
        type: ItemType,
        reportedBy: String
    ) {
        viewModelScope.launch {
            _reportState.value = ReportState.Loading
            try {
                val item = Item(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    category = try { ItemCategory.valueOf(category.uppercase().replace(" ", "_")) } catch (e: Exception) { ItemCategory.OTHER },
                    status = if (type == ItemType.LOST) ItemStatus.LOST else ItemStatus.FOUND,
                    location = location,
                    date = date,
                    time = time,
                    description = description,
                    imageUrl = "", // Placeholder
                    reportedBy = reportedBy,
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
}

sealed class ReportState {
    object Idle : ReportState()
    object Loading : ReportState()
    object Success : ReportState()
    data class Error(val message: String) : ReportState()
}
