package com.tagger.phototagger.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tagger.phototagger.data.local.entity.AnnotatedImageEntity
import com.tagger.phototagger.data.local.entity.ImageTagEntity

@Database(
    entities = [AnnotatedImageEntity::class, ImageTagEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context = context.applicationContext,
                    AppDatabase::class.java, "tagger_database").build()
                INSTANCE = instance
                instance
            }
        }
    }
}