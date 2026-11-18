package com.lucasoliveira.bliss.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lucasoliveira.bliss.presentation.snackbar.SnackbarControllerImpl
import com.lucasoliveira.bliss.presentation.snackbar.SnackbarMessage
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BlissScaffoldTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val snackbarController = SnackbarControllerImpl()

    private fun setScaffold() {
        composeRule.setContent {
            BlissTheme {
                BlissScaffold(snackbarController = snackbarController) {
                    Text(text = "Content")
                }
            }
        }
    }

    @Test
    fun `given scaffold, when shown, then show the content`() {
        setScaffold()

        composeRule.onNodeWithText("Content").assertIsDisplayed()
    }

    @Test
    fun `given message sent to controller, when scaffold is shown, then show it in a snackbar`() {
        setScaffold()

        runBlocking { snackbarController.show(SnackbarMessage("User unknown-user not found")) }

        composeRule.onNodeWithText("User unknown-user not found").assertIsDisplayed()
    }

    @Test
    fun `given message without text, when scaffold is shown, then show the generic error`() {
        setScaffold()

        runBlocking { snackbarController.show(SnackbarMessage(null)) }

        composeRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    @Test
    fun `given snackbar shown, when dismiss is clicked, then hide it`() {
        setScaffold()
        runBlocking { snackbarController.show(SnackbarMessage("No internet connection")) }
        composeRule.onNodeWithText("No internet connection").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Dismiss").performClick()

        composeRule.onNodeWithText("No internet connection").assertDoesNotExist()
    }
}
