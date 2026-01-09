package com.tagger.phototagger.data

import ImageDao
import android.content.Context
import android.net.Uri
import android.util.Base64
import com.example.kotlintutorials.R
import com.tagger.phototagger.data.local.LocalFileManager
import com.tagger.phototagger.data.local.entity.AnnotatedImageEntity
import com.tagger.phototagger.data.remote.NetworkModule
import com.tagger.phototagger.data.remote.dto.AnnotatedImageRequest
import com.tagger.phototagger.model.Artwork

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ImageRepository @Inject constructor(
    private val localFileManager: LocalFileManager,
    private val imageDao: ImageDao,
    @ApplicationContext private val context: Context
) {

    private val resourceBaseUri: String
        get() = "android.resource://${context.packageName}/"

    suspend fun saveImage(uri: String, title: String) {
        withContext(Dispatchers.IO) {
            val path = localFileManager.storeImage(uriString = uri, filename = title)

            if (path != null) {
                val record = AnnotatedImageEntity(title = title, imagePath = path, imageSource = uri)
                imageDao.insertImage(record)
            }

        }

    }


    fun getSavedImages() = imageDao.getAllImages()


    fun getSampleArtworks(): List<Artwork> {
        return listOf(
            Artwork(imagePath = getResourceUri(R.drawable.india1)),
            Artwork(imagePath = getResourceUri(R.drawable.india2)),
            Artwork(imagePath = getResourceUri(R.drawable.india3)),
            Artwork(imagePath = getResourceUri(R.drawable.india4)),
            Artwork(imagePath = getResourceUri(R.drawable.india5)),
            Artwork(imagePath = getResourceUri(R.drawable.india6)),
            Artwork(imagePath = getResourceUri(R.drawable.india7)),
            Artwork(imagePath = getResourceUri(R.drawable.india8)),
            Artwork(imagePath = getResourceUri(R.drawable.india9)),

            )
    }

    private fun getResourceUri(resId: Int): String {
        return "$resourceBaseUri$resId"
    }


    suspend fun getSavedRecordByUri(sourcePath: String): AnnotatedImageEntity? = withContext(Dispatchers.IO) {
        return@withContext imageDao.findRecordBySource(sourcePath)
    }

    suspend fun generateAiTitle(imageUri: String): String{
        return withContext(Dispatchers.IO){
            try {
                val inputStream = context.contentResolver.openInputStream(Uri.parse(imageUri))
                val bytes = inputStream?.readBytes() ?: return@withContext "Untitled"
                val base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)

                val response = NetworkModule.geminiFunctionsApi.annotateArtworkFromImage(
                    AnnotatedImageRequest(image64 = base64Image, mimeType = "image/jpeg")
                )

                response.raw
            } catch (e: Exception){
                null
                "Error generating title"
            }
        }
    }


}