package com.tagger.phototagger.data.local

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class LocalFileManager @Inject constructor(@ApplicationContext private val context: Context) {

    fun storeImage(uriString: String, filename: String): String? {
        val uri = Uri.parse(uriString)
        val file = File(context.filesDir, filename)

        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}