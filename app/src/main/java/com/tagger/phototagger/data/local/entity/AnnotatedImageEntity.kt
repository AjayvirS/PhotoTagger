package com.tagger.phototagger.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "annotated_images",   indices = [Index("createdAt"), Index("title")])
data class AnnotatedImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val imagePath: String,
    val imageSource: String,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING",
    val thumbPath: String?
)