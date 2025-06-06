package com.example.lombatif.component.adminDashboard

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.IOException

object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload", ".tmp", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}