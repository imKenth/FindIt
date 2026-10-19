package com.findit.app.data.repository

import com.findit.app.domain.model.Item
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class FirestoreRepositoryImpl(
    private val firestore: FirebaseFirestore = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        FirebaseFirestore.getInstance(com.google.firebase.FirebaseApp.getInstance())
    }
) : ItemRepository {

    private val localItems = mutableListOf<Item>()
    private val itemsCollection by lazy { firestore.collection("items") }

    override fun getAllItems(): Flow<List<Item>> {
        return try {
            itemsCollection.snapshots().map { snapshot ->
                val remoteItems = snapshot.toObjects(Item::class.java)
                (localItems + remoteItems).distinctBy { it.id }
            }.catch {
                emit(localItems.toList())
            }
        } catch (e: Exception) {
            flowOf(localItems.toList())
        }
    }

    override fun getItemById(id: String): Flow<Item?> {
        val local = localItems.find { it.id == id }
        if (local != null) return flowOf(local)

        return try {
            itemsCollection.document(id).snapshots().map { snapshot ->
                snapshot.toObject(Item::class.java) ?: localItems.find { it.id == id }
            }.catch {
                emit(localItems.find { it.id == id })
            }
        } catch (e: Exception) {
            flowOf(localItems.find { it.id == id })
        }
    }

    override suspend fun reportItem(item: Item) {
        // Always store locally first so app works offline / instantly
        localItems.add(0, item)

        // Try syncing to Firestore in background with 3-second timeout
        try {
            withTimeoutOrNull(3000) {
                if (item.id.isNotEmpty()) {
                    itemsCollection.document(item.id).set(item).await()
                } else {
                    itemsCollection.add(item).await()
                }
            }
        } catch (e: Exception) {
            // Firestore write failed or timed out; item is preserved in localItems
        }
    }
}
