package com.tagger.phototagger.data.local

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.tagger.phototagger.data.local.entity.AnnotatedImageEntity
import com.tagger.phototagger.data.local.entity.ImageTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: AnnotatedImageEntity): Long

    @Query("SELECT * FROM annotated_images ORDER BY createdAt DESC")
    fun observeAllImages(): Flow<List<AnnotatedImageEntity>>

    @Query ("SELECT * FROM annotated_images WHERE imagePath = :imagePath LIMIT 1")
    suspend fun findRecordByImagePath(imagePath: String): AnnotatedImageEntity?

    @Query("SELECT * FROM annotated_images WHERE imageSource = :originalPath LIMIT 1")
    fun observeRecordBySource(originalPath: String): Flow<AnnotatedImageEntity?>




    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<ImageTagEntity>)

    @Transaction
    @Query("""
    SELECT * FROM annotated_images 
    WHERE id IN (
        SELECT imageId FROM image_tag 
        WHERE name LIKE '%' || :q || '%'
    )
    ORDER BY createdAt DESC
""")
    fun searchByTag(q: String): Flow<List<AnnotatedImageWithTags>>

}


data class AnnotatedImageWithTags(
    @Embedded val image: AnnotatedImageEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "imageId"
    )
    val tags: List<ImageTagEntity>
)
