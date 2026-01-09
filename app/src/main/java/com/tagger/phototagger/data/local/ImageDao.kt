
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tagger.phototagger.data.local.model.AnnotatedImage
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: AnnotatedImage)

    @Query("SELECT * FROM annotated_images ORDER BY timestamp DESC")
    fun getAllImages(): Flow<List<AnnotatedImage>>

    @Query ("SELECT * FROM annotated_images WHERE imagePath = :imagePath LIMIT 1")
    suspend fun findRecordByImagePath(imagePath: String): AnnotatedImage?

    @Query("SELECT * FROM annotated_images WHERE imageSource = :originalPath LIMIT 1")
    suspend fun findRecordBySource(originalPath: String): AnnotatedImage?


}