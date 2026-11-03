package com.findit.app.di

import com.findit.app.data.repository.AuthRepository
import com.findit.app.data.repository.FirebaseAuthRepositoryImpl
import com.findit.app.data.repository.FirestoreRepositoryImpl
import com.findit.app.data.repository.ItemRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Simple Dependency Injection module
 * Provides singleton instances of repositories
 */
object AppModule {
    
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    
    val authRepository: AuthRepository by lazy {
        FirebaseAuthRepositoryImpl(firebaseAuth)
    }
    
    val itemRepository: ItemRepository by lazy {
        FirestoreRepositoryImpl(firestore)
    }
}
