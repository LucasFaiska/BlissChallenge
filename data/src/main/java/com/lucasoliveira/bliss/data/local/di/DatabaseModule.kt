package com.lucasoliveira.bliss.data.local.di

import android.content.Context
import androidx.room.Room
import com.lucasoliveira.bliss.data.local.BlissDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    private const val DATABASE_NAME = "bliss.db"

    @Provides
    @Singleton
    fun provideBlissDatabase(@ApplicationContext context: Context): BlissDatabase =
        Room.databaseBuilder(context, BlissDatabase::class.java, DATABASE_NAME).build()
}
