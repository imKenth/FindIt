package com.findit.app.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
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
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user?.toDomainUser()
            if (user != null) Result.success(user)
            else Result.failure(Exception("User not found after sign in"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            )?.await()
            val user = result.user?.toDomainUser()?.copy(name = name)
            if (user != null) Result.success(user)
            else Result.failure(Exception("Failed to create user"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    // Extension function to map Firebase User to our Domain User
    private fun com.google.firebase.auth.FirebaseUser.toDomainUser(): User {
        return User(
            id = uid,
            name = displayName ?: "User",
            email = email ?: "",
            avatar = photoUrl?.toString() ?: "",
            reportsCount = 0,
            claimsCount = 0,
            resolvedCount = 0,
        )
    }
}