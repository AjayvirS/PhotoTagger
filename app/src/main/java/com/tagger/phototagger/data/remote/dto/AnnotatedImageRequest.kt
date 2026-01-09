package com.tagger.phototagger.data.remote.dto


data class AnnotatedImageRequest(
    val mimeType: String,
    val image64: String
)