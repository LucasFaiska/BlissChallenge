package com.lucasoliveira.bliss.data.local.di

import com.lucasoliveira.bliss.data.local.LocalDataSource
import com.lucasoliveira.bliss.data.local.LocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface LocalDataModule {

    @Binds
    @Singleton
    fun bindLocalDataSource(localDataSourceImpl: LocalDataSourceImpl): LocalDataSource
}
