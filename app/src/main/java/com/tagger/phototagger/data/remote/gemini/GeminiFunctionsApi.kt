package com.tagger.phototagger.data.remote.gemini

import com.tagger.phototagger.data.remote.dto.AnnotatedImageRequest
import com.tagger.phototagger.data.remote.dto.AnnotatedImageResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GeminiFunctionsApi {

    @POST("annotateArtwork")
    suspend fun annotateArtworkFromImage(@Body body: AnnotatedImageRequest): AnnotatedImageResponse

}