package com.findit.app.data.repository

import com.findit.app.domain.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getAllItems(): Flow<List<Item>>
    fun getItemById(id: String): Flow<Item?>
    suspend fun reportItem(item: Item)
}