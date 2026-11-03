package com.findit.app.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.findit.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>

    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(name: String, email: String, password: String, photoUrl: String? = null): Result<User>
    suspend fun signInWithGoogle(
        idToken: String,
        email: String,
        name: String? = null,
        photoUrl: String? = null
    ): Result<User>
    suspend fun updateGoogleProfile(name: String, photoUrl: String?): Result<Unit>
    suspend fun updateProfilePhoto(uri: Uri, contentResolver: ContentResolver): Result<String>
    suspend fun signOut()
}

object AuthEmailPolicy {
    const val DOMAIN_ERROR =
        "Only official @sjp2cd.edu.ph school email addresses are allowed."
    const val VERIFICATION_ERROR =
        "Please check your @sjp2cd.edu.ph inbox and click the verification link before signing in."
    const val VERIFICATION_SENT_MESSAGE =
        "Verification email sent. Please check your @sjp2cd.edu.ph inbox and click the verification link before signing in."

    private const val SCHOOL_DOMAIN = "@sjp2cd.edu.ph"
    private const val ADMIN_EMAIL = "admin@school.edu"

    fun isAllowed(email: String): Boolean {
        val normalizedEmail = email.trim()
        return normalizedEmail.equals(ADMIN_EMAIL, ignoreCase = true) ||
            normalizedEmail.endsWith(SCHOOL_DOMAIN, ignoreCase = true)
    }

    fun isAdmin(email: String?): Boolean =
        email?.trim()?.equals(ADMIN_EMAIL, ignoreCase = true) == true
}
