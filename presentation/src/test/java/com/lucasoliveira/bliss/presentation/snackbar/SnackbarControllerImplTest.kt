package com.lucasoliveira.bliss.presentation.snackbar

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SnackbarControllerImplTest {

    private val snackbarController = SnackbarControllerImpl()

    @Test
    fun `given a collector, when show is called, then emit the message`() = runTest {
        snackbarController.messages.test {
            snackbarController.show(SnackbarMessage("No internet connection"))

            assertEquals(SnackbarMessage("No internet connection"), awaitItem())
        }
    }

    @Test
    fun `given no collector yet, when show is called, then deliver the message once collected`() = runTest {
        snackbarController.show(SnackbarMessage("No internet connection"))

        snackbarController.messages.test {
            assertEquals(SnackbarMessage("No internet connection"), awaitItem())
        }
    }

    @Test
    fun `given several messages, when show is called, then emit them in order`() = runTest {
        snackbarController.messages.test {
            snackbarController.show(SnackbarMessage("First"))
            snackbarController.show(SnackbarMessage("Second", actionLabel = "Retry"))

            assertEquals(SnackbarMessage("First"), awaitItem())
            assertEquals(SnackbarMessage("Second", actionLabel = "Retry"), awaitItem())
        }
    }
}
