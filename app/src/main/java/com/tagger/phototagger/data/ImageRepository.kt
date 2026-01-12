package com.tagger.phototagger.data

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.tagger.phototagger.data.local.ImageDao
import com.tagger.phototagger.data.local.LocalFileManager
import com.tagger.phototagger.data.local.entity.AnnotatedImageEntity
import com.tagger.phototagger.data.remote.NetworkModule
import com.tagger.phototagger.data.remote.dto.AnnotatedImageRequest
import com.tagger.phototagger.data.util.sanitizeFilename
import com.tagger.phototagger.data.util.toUriFlexible
import com.tagger.phototagger.enum.ProcessingStatus
import com.tagger.phototagger.model.Artwork

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

enum class ImageSource { PICKER, CAMERA, REMOTE }


class ImageRepository @Inject constructor(
    private val localFileManager: LocalFileManager,
    private val imageDao: ImageDao,
    @ApplicationContext private val context: Context
) {

    private val resourceBaseUri: String
        get() = "android.resource://${context.packageName}/"


    fun observeSavedRecordByUri(sourcePath: String): Flow<AnnotatedImageEntity?> {
        return imageDao.observeRecordBySource(sourcePath)
    }

    suspend fun saveImage(
        uri: String,
        title: String
    ): String? = withContext(Dispatchers.IO) {
        try {
            val storedPath = localFileManager.storeImage(
                uri = uri.toUriFlexible(),
                filename = sanitizeFilename(title)
            ) ?: return@withContext null

            val record = AnnotatedImageEntity(
                title = title,
                imagePath = storedPath,
                imageSource = ImageSource.PICKER.name,
                status = ProcessingStatus.PENDING.name,
                thumbPath = null
            )
            imageDao.insertImage(record)

            storedPath
        } catch (t: Throwable) {
            Log.e("ImageRepository", "Failed to save image: ${t.message}")
            null
        }
    }

    suspend fun generateAiTitle(imageUri: String): String = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(imageUri)
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return@withContext "Untitled"

            val base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)

            val response = NetworkModule.geminiFunctionsApi.annotateArtworkFromImage(
                AnnotatedImageRequest(image64 = base64Image, mimeType = "image/jpeg")
            )

            response.raw
        } catch (e: Exception) {
            Log.e("ImageRepository", "AI Generation failed", e)
            "Error generating title"
        }
    }


    fun getSampleArtworks(): List<Artwork> {
        return (1..9).map { i ->
            val resId = context.resources.getIdentifier("india$i", "drawable", context.packageName)
            Artwork(imagePath = "$resourceBaseUri$resId")
        }
    }

    fun getSavedImages() = imageDao.observeAllImages()
}