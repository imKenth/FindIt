package com.findit.app.data.repository

import android.content.ContentResolver
import android.net.Uri
import com.findit.app.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class FirebaseAuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = try {
        FirebaseAuth.getInstance()
    } catch (e: Exception) {
        FirebaseAuth.getInstance(com.google.firebase.FirebaseApp.getInstance())
    }
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomainUser())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun updateProfilePhoto(uri: Uri, contentResolver: ContentResolver): Result<String> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("You must be signed in to upload a profile photo"))
            val downloadUrl = uploadToCloudinary(uri, contentResolver, user.uid)
            user.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setPhotoUri(Uri.parse(downloadUrl))
                    .build()
            ).await()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadToCloudinary(
        uri: Uri,
        contentResolver: ContentResolver,
        userId: String
    ): String = withContext(Dispatchers.IO) {
        val boundary = "----FindItCloudinary${System.currentTimeMillis()}"
        val connection = URL(CLOUDINARY_UPLOAD_URL).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

        try {
            DataOutputStream(connection.outputStream).use { output ->
                writeFormField(output, boundary, "upload_preset", CLOUDINARY_UPLOAD_PRESET)
                writeFormField(output, boundary, "public_id", "profile_$userId")
                output.writeBytes("--$boundary\r\n")
                output.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"profile.jpg\"\r\n")
                output.writeBytes("Content-Type: ${contentResolver.getType(uri) ?: "image/jpeg"}\r\n\r\n")
                contentResolver.openInputStream(uri)?.use { input ->
                    input.copyTo(output)
                } ?: throw IllegalArgumentException("Could not read selected image")
                output.writeBytes("\r\n--$boundary--\r\n")
            }

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            JSONObject(response).getString("secure_url")
        } finally {
            connection.disconnect()
        }
    }

    private fun writeFormField(output: DataOutputStream, boundary: String, name: String, value: String) {
        output.writeBytes("--$boundary\r\n")
        output.writeBytes("Content-Disposition: form-data; name=\"$name\"\r\n\r\n")
        output.writeBytes("$value\r\n")
    }

    private companion object {
        const val CLOUDINARY_UPLOAD_URL =
            "https://api.cloudinary.com/v1_1/do0pueye3/image/upload"
        const val CLOUDINARY_UPLOAD_PRESET = "findit_profile_imgs"
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        val normalizedEmail = email.trim()
        if (!AuthEmailPolicy.isAllowed(normalizedEmail)) {
            return Result.failure(IllegalArgumentException(AuthEmailPolicy.DOMAIN_ERROR))
        }

        var authenticated = false
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(normalizedEmail, password).await()
            val firebaseUser = result.user
                ?: return Result.failure(IllegalStateException("User not found after sign in"))
            authenticated = true

            firebaseUser.reload().await()
            val signedInUser = firebaseAuth.currentUser ?: firebaseUser
            val authenticatedEmail = signedInUser.email.orEmpty()
            if (!AuthEmailPolicy.isAllowed(authenticatedEmail)) {
                firebaseAuth.signOut()
                return Result.failure(IllegalArgumentException(AuthEmailPolicy.DOMAIN_ERROR))
            }
            if (!signedInUser.isEmailVerified && !AuthEmailPolicy.isAdmin(authenticatedEmail)) {
                firebaseAuth.signOut()
                return Result.failure(IllegalStateException(AuthEmailPolicy.VERIFICATION_ERROR))
            }

            Result.success(signedInUser.toDomainUser())
        } catch (e: Exception) {
            if (authenticated) firebaseAuth.signOut()
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(
        idToken: String,
        email: String,
        name: String?,
        photoUrl: String?
    ): Result<User> {
        if (!AuthEmailPolicy.isAllowed(email)) {
            return Result.failure(IllegalArgumentException(AuthEmailPolicy.DOMAIN_ERROR))
        }

        var authenticated = false
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            authenticated = true
            val firebaseUser = result.user
                ?: return Result.failure(IllegalStateException("Failed to sign in with Google"))
            val authenticatedEmail = firebaseUser.email.orEmpty()
            if (!AuthEmailPolicy.isAllowed(authenticatedEmail)) {
                firebaseAuth.signOut()
                return Result.failure(IllegalArgumentException(AuthEmailPolicy.DOMAIN_ERROR))
            }

            if (!name.isNullOrEmpty() || !photoUrl.isNullOrEmpty()) {
                val builder = UserProfileChangeRequest.Builder()
                if (!name.isNullOrEmpty()) builder.setDisplayName(name)
                if (!photoUrl.isNullOrEmpty()) builder.setPhotoUri(Uri.parse(photoUrl))
                firebaseUser.updateProfile(builder.build()).await()
            }

            Result.success(firebaseUser.toDomainUser())
        } catch (e: Exception) {
            if (authenticated) firebaseAuth.signOut()
            Result.failure(e)
        }
    }

    override suspend fun signUp(name: String, email: String, password: String, photoUrl: String?): Result<User> {
        val normalizedEmail = email.trim()
        if (!AuthEmailPolicy.isAllowed(normalizedEmail)) {
            return Result.failure(IllegalArgumentException(AuthEmailPolicy.DOMAIN_ERROR))
        }

        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(normalizedEmail, password).await()
            val firebaseUser = result.user
                ?: return Result.failure(IllegalStateException("Failed to create user"))

            try {
                val profileBuilder = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                if (!photoUrl.isNullOrEmpty()) {
                    profileBuilder.setPhotoUri(Uri.parse(photoUrl))
                }
                firebaseUser.updateProfile(profileBuilder.build()).await()
                firebaseUser.sendEmailVerification().await()

                Result.success(
                    firebaseUser.toDomainUser().copy(
                        name = name,
                        avatar = photoUrl ?: firebaseUser.photoUrl?.toString() ?: ""
                    )
                )
            } finally {
                firebaseAuth.signOut()
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateGoogleProfile(name: String, photoUrl: String?): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No user logged in"))
            val builder = UserProfileChangeRequest.Builder().setDisplayName(name)
            if (!photoUrl.isNullOrEmpty()) {
                builder.setPhotoUri(Uri.parse(photoUrl))
            }
            user.updateProfile(builder.build()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
        try {
            val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
            ).build()
            val client = com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(
                com.findit.app.FindItApplication.instance, gso
            )
            client.signOut().await()
            client.revokeAccess().await()
        } catch (e: Exception) {
            // Ignore if Google Play Services client is not initialized or offline
        }
    }

    // Extension function to map Firebase User to our Domain User
    private fun com.google.firebase.auth.FirebaseUser.toDomainUser(): User {
        val googleAvatar = photoUrl?.toString()
        val avatarUrl = if (!googleAvatar.isNullOrEmpty()) googleAvatar else "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&h=200&fit=crop&auto=format"
        val formattedName = if (!displayName.isNullOrBlank()) displayName!! else email?.substringBefore("@")?.replace(".", " ") ?: "User"
        return User(
            id = uid,
            name = formattedName,
            email = email ?: "",
            avatar = avatarUrl,
            reportsCount = 0,
            claimsCount = 0,
            resolvedCount = 0,
        )
    }
}