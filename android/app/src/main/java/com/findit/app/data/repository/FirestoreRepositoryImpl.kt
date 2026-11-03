package com.findit.app.data.repository

import android.net.Uri
import com.findit.app.data.remote.MockData
import com.findit.app.domain.model.Item
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepositoryImpl(
    private val firestore: FirebaseFirestore = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        FirebaseFirestore.getInstance(com.google.firebase.FirebaseApp.getInstance())
    }
) : ItemRepository {

    private val itemsCollection by lazy { firestore.collection("items") }
    private val storage by lazy { FirebaseStorage.getInstance() }

    override fun getAllItems(): Flow<List<Item>> {
        return try {
            itemsCollection.orderBy("date", Query.Direction.DESCENDING).snapshots().map { snapshot ->
                val remoteItems = snapshot.toObjects(Item::class.java)
                (remoteItems + MockData.items).distinctBy { it.id }
            }.catch {
                emit(MockData.items)
            }
        } catch (e: Exception) {
            flowOf(MockData.items)
        }
    }

    override fun getItemById(id: String): Flow<Item?> {
        val local = MockData.items.find { it.id == id }
        return try {
            itemsCollection.document(id).snapshots().map { snapshot ->
                snapshot.toObject(Item::class.java) ?: local
            }.catch {
                emit(local)
            }
        } catch (e: Exception) {
            flowOf(local)
        }
    }

    override suspend fun reportItem(item: Item) {
        if (item.id.isNotEmpty()) {
            itemsCollection.document(item.id).set(item).await()
        } else {
            itemsCollection.add(item).await()
        }
    }

    override suspend fun uploadItemImage(itemId: String, imageUri: Uri): String {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("You must be signed in to upload an item photo.")
        val imageReference = storage.reference.child("item_images/$userId/$itemId")
        imageReference.putFile(imageUri).await()
        return imageReference.downloadUrl.await().toString()
    }
}
