package com.findit.app.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.findit.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>

    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(name: String, email: String, password: String): Result<User>
    suspend fun updateProfilePhoto(uri: Uri, contentResolver: ContentResolver): Result<String>
    suspend fun signOut()

}