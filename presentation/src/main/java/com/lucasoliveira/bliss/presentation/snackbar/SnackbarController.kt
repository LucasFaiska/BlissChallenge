package com.lucasoliveira.bliss.presentation.snackbar

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

interface SnackbarController {

    val messages: Flow<SnackbarMessage>

    suspend fun show(message: SnackbarMessage)
}

@Singleton
class SnackbarControllerImpl @Inject constructor() : SnackbarController {

    private val channel = Channel<SnackbarMessage>(Channel.BUFFERED)

    override val messages: Flow<SnackbarMessage> = channel.receiveAsFlow()

    override suspend fun show(message: SnackbarMessage) = channel.send(message)
}
