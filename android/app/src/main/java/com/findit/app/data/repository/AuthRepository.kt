package com.findit.app.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.findit.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>

    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(name: String, email: String, password: String, photoUrl: String? = null): Result<User>
    suspend fun signInWithGoogle(idToken: String, name: String? = null, photoUrl: String? = null): Result<User>
    suspend fun updateGoogleProfile(name: String, photoUrl: String?): Result<Unit>
    suspend fun updateProfilePhoto(uri: Uri, contentResolver: ContentResolver): Result<String>
    suspend fun signOut()
}
