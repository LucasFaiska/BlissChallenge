package com.lucasoliveira.bliss.presentation.snackbar

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface SnackbarModule {

    @Binds
    @Singleton
    fun bindSnackbarController(snackbarControllerImpl: SnackbarControllerImpl): SnackbarController
}
