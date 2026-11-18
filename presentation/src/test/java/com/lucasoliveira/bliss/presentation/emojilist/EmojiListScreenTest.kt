package com.lucasoliveira.bliss.presentation.emojilist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lucasoliveira.bliss.domain.model.Emoji
import com.lucasoliveira.bliss.presentation.component.LOADING_TEST_TAG
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class EmojiListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val intents = mutableListOf<EmojiListIntent>()

    private val thumbsUp = Emoji(name = "+1", url = "https://emoji/thumbs-up.png")
    private val thumbsDown = Emoji(name = "-1", url = "https://emoji/thumbs-down.png")

    private fun setScreen(state: EmojiListUiState) {
        composeRule.setContent {
            BlissTheme {
                EmojiListScreen(uiState = state, onIntent = { intents += it })
            }
        }
    }

    @Test
    fun `given Loading state, when screen is shown, then show loading`() {
        setScreen(EmojiListUiState.Loading)

        composeRule.onNodeWithTag(LOADING_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun `given Success state, when screen is shown, then show each emoji`() {
        setScreen(EmojiListUiState.Success(listOf(thumbsUp, thumbsDown)))

        composeRule.onNodeWithContentDescription("+1").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("-1").assertIsDisplayed()
    }

    @Test
    fun `given Success state, when an emoji is clicked, then send EmojiClicked with that emoji`() {
        setScreen(EmojiListUiState.Success(listOf(thumbsUp, thumbsDown)))

        composeRule.onNodeWithContentDescription("-1").performClick()

        assertEquals(listOf(EmojiListIntent.EmojiClicked(thumbsDown)), intents)
    }

    @Test
    fun `given Error state, when screen is shown, then show title and message`() {
        setScreen(EmojiListUiState.Error("API rate limit reached. Try again at 14:32"))

        composeRule.onNodeWithText("Oops!").assertIsDisplayed()
        composeRule.onNodeWithText("API rate limit reached. Try again at 14:32").assertIsDisplayed()
    }

    @Test
    fun `given Error state without message, when screen is shown, then show generic message`() {
        setScreen(EmojiListUiState.Error(null))

        composeRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    @Test
    fun `given Error state, when retry is clicked, then send LoadEmojis`() {
        setScreen(EmojiListUiState.Error("No internet connection"))

        composeRule.onNodeWithText("RETRY").performClick()

        assertEquals(listOf(EmojiListIntent.LoadEmojis), intents)
    }
}
