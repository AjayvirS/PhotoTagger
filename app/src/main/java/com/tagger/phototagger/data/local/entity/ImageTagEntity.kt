package com.tagger.phototagger.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "image_tag",
    indices = [Index("imageId"), Index("name")],
    foreignKeys = [
        ForeignKey(
            entity = AnnotatedImageEntity::class,
            parentColumns = ["id"],
            childColumns = ["imageId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ImageTagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageId: Long,
    val name: String
)