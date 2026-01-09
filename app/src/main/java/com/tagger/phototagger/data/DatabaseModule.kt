package com.tagger.phototagger.data

import ImageDao
import android.content.Context
import com.tagger.phototagger.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getDatabase(context = context)

    @Provides
    fun provideImageDao(db: AppDatabase): ImageDao = db.imageDao()
}