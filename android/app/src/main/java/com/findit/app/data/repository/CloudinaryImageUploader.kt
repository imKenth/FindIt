package com.findit.app.data.repository

import android.content.ContentResolver
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

internal object CloudinaryImageUploader {
    private const val UPLOAD_URL = "https://api.cloudinary.com/v1_1/do0pueye3/image/upload"
    private const val UPLOAD_PRESET = "findit_profile_imgs"

    suspend fun upload(
        uri: Uri,
        contentResolver: ContentResolver,
        publicId: String
    ): String = withContext(Dispatchers.IO) {
        val boundary = "----FindItCloudinary${System.currentTimeMillis()}"
        val connection = URL(UPLOAD_URL).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

        try {
            DataOutputStream(connection.outputStream).use { output ->
                writeFormField(output, boundary, "upload_preset", UPLOAD_PRESET)
                writeFormField(output, boundary, "public_id", publicId)
                output.writeBytes("--$boundary\r\n")
                output.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"\r\n")
                output.writeBytes("Content-Type: ${contentResolver.getType(uri) ?: "image/jpeg"}\r\n\r\n")
                contentResolver.openInputStream(uri)?.use { input ->
                    input.copyTo(output)
                } ?: throw IllegalArgumentException("Could not read selected image")
                output.writeBytes("\r\n--$boundary--\r\n")
            }

            val responseCode = connection.responseCode
            val responseStream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            val response = responseStream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (responseCode !in 200..299) {
                val message = runCatching {
                    JSONObject(response).getJSONObject("error").getString("message")
                }.getOrNull()
                throw IllegalStateException(message ?: "Cloudinary upload failed (HTTP $responseCode)")
            }
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
}
