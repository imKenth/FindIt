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

    override fun getItemById(id: String): Flow<Item?> {
        TODO("Not yet implemented")
    }

    override suspend fun reportItem(item: Item) {
        TODO("Not yet implemented")
    }
    // ... implement other methods using MockData
}