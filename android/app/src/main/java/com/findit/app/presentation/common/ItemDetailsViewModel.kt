package com.findit.app.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findit.app.domain.model.Item
import com.findit.app.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemDetailsViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _item = MutableStateFlow<Item?>(null)
    val item = _item.asStateFlow()

    fun getItem(id: String) {
        viewModelScope.launch {
            repository.getItemById(id).collect { _item.value = it }
        }
    }
}
