package com.findit.app.data.repository

import com.findit.app.domain.model.Item
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ItemRepository {

    private val itemsCollection = firestore.collection("items")

    override fun getAllItems(): Flow<List<Item>> {
        // .snapshots() provides a real-time Flow of the collection
        return itemsCollection.snapshots().map { snapshot ->
            // toObjects() maps Firestore documents to your Kotlin Item class
            snapshot.toObjects(Item::class.java)
        }
    }

    override fun getItemById(id: String): Flow<Item?> {
        return itemsCollection.document(id).snapshots().map { snapshot ->
            snapshot.toObject(Item::class.java)
        }
    }

    override suspend fun reportItem(item: Item) {
        // If the item already has an ID, we use set(), otherwise we add() a new document
        if (item.id.isNotEmpty()) {
            itemsCollection.document(item.id).set(item).await()
        } else {
            itemsCollection.add(item).await()
        }
    }
}
