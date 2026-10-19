package com.findit.app.data.repository

import com.findit.app.data.remote.MockData
import com.findit.app.data.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import com.findit.app.domain.model.Item
import kotlinx.coroutines.flow.flow

class MockItemRepositoryImpl : ItemRepository {
    override fun getAllItems(): Flow<List<Item>> = flow {
        emit(MockData.items)
    }

    override fun getItemById(id: String): Flow<Item?> = flow {
        emit(MockData.items.find { it.id == id })
    }

    override suspend fun reportItem(item: Item) {
        // In a real implementation, this would add to a mutable list
        // For now, mock data is immutable so we just simulate success
        // The Firestore implementation will handle actual persistence
    }
}