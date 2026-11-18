package com.lucasoliveira.bliss.presentation.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val intents = mutableListOf<HomeIntent>()

    private fun setScreen(state: HomeUiState) {
        composeRule.setContent {
            BlissTheme {
                HomeScreen(uiState = state, onIntent = { intents += it })
            }
        }
    }

    @Test
    fun `given Idle state, when screen is shown, then show default image and all actions`() {
        setScreen(HomeUiState.Idle)

        composeRule.onNodeWithContentDescription("Default image").assertIsDisplayed()
        composeRule.onNodeWithText("RANDOM EMOJI").assertIsDisplayed()
        composeRule.onNodeWithText("EMOJI LIST").assertIsDisplayed()
        composeRule.onNodeWithText("Insert github username").assertIsDisplayed()
        composeRule.onNodeWithText("AVATAR LIST").assertIsDisplayed()
        composeRule.onNodeWithText("GOOGLE REPOS").assertIsDisplayed()
    }

    @Test
    fun `given Success state, when screen is shown, then show selected image instead of default`() {
        setScreen(HomeUiState.Success(imageUrl = "https://avatars/223156"))

        composeRule.onNodeWithContentDescription("Selected image").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Default image").assertDoesNotExist()
    }

    @Test
    fun `given Loading state, when screen is shown, then show loading and disable random emoji and search`() {
        setScreen(HomeUiState.Loading)

        composeRule.onNodeWithTag(LOADING_TEST_TAG).assertIsDisplayed()
        composeRule.onNodeWithText("RANDOM EMOJI").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Search").assertIsNotEnabled()
    }

    @Test
    fun `given home, when random emoji is clicked, then send RandomEmojiClicked`() {
        setScreen(HomeUiState.Idle)

        composeRule.onNodeWithText("RANDOM EMOJI").performClick()

        assertEquals(listOf(HomeIntent.RandomEmojiClicked), intents)
    }

    @Test
    fun `given home, when emoji list is clicked, then send EmojiListClicked`() {
        setScreen(HomeUiState.Idle)

        composeRule.onNodeWithText("EMOJI LIST").performClick()

        assertEquals(listOf(HomeIntent.EmojiListClicked), intents)
    }

    @Test
    fun `given home, when avatar list is clicked, then send AvatarListClicked`() {
        setScreen(HomeUiState.Idle)

        composeRule.onNodeWithText("AVATAR LIST").performClick()

        assertEquals(listOf(HomeIntent.AvatarListClicked), intents)
    }

    @Test
    fun `given home, when google repos is clicked, then send GoogleReposClicked`() {
        setScreen(HomeUiState.Idle)

        composeRule.onNodeWithText("GOOGLE REPOS").performClick()

        assertEquals(listOf(HomeIntent.GoogleReposClicked), intents)
    }

    @Test
    fun `given typed username, when search is clicked, then send SearchClicked with the username`() {
        setScreen(HomeUiState.Idle)
        composeRule.onNode(hasSetTextAction()).performTextInput("blissapps")

        composeRule.onNodeWithContentDescription("Search").performClick()

        assertEquals(listOf(HomeIntent.SearchClicked("blissapps")), intents)
    }
}
