package com.lucasoliveira.bliss.data.remote.di

import com.lucasoliveira.bliss.data.remote.RemoteDataSource
import com.lucasoliveira.bliss.data.remote.RemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface RemoteDataModule {

    @Binds
    @Singleton
    fun bindRemoteDataSource(remoteDataSourceImpl: RemoteDataSourceImpl): RemoteDataSource
}
