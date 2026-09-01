package com.findit.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findit.app.domain.model.Item
import com.findit.app.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: ItemRepository) : ViewModel() {
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items = _items.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllItems().collect { _items.value = it }
        }
    }
}