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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lucasoliveira.bliss.presentation.component.LOADING_TEST_TAG
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
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
    fun given_Idle_state_when_screen_is_shown_then_show_default_image_and_all_actions() {
        setScreen(HomeUiState.Idle)

        composeRule.onNodeWithContentDescription("Default image").assertIsDisplayed()
        composeRule.onNodeWithText("RANDOM EMOJI").assertIsDisplayed()
        composeRule.onNodeWithText("EMOJI LIST").assertIsDisplayed()
        composeRule.onNodeWithText("Insert github username").assertIsDisplayed()
        composeRule.onNodeWithText("AVATAR LIST").assertIsDisplayed()
        composeRule.onNodeWithText("GOOGLE REPOS").assertIsDisplayed()
    }

    @Test
    fun given_Success_state_when_screen_is_shown_then_show_selected_image_instead_of_default() {
        setScreen(HomeUiState.Success(imageUrl = "https://avatars/223156"))

        composeRule.onNodeWithContentDescription("Selected image").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Default image").assertDoesNotExist()
    }

    @Test
    fun given_Loading_state_when_screen_is_shown_then_show_loading_and_disable_random_emoji_and_search() {
        setScreen(HomeUiState.Loading)

        composeRule.onNodeWithTag(LOADING_TEST_TAG).assertIsDisplayed()
        composeRule.onNodeWithText("RANDOM EMOJI").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Search").assertIsNotEnabled()
    }

    @Test
    fun given_home_when_random_emoji_is_clicked_then_send_RandomEmojiClicked() {
        setScreen(HomeUiState.Idle)

        composeRule.onNodeWithText("RANDOM EMOJI").performClick()

        assertEquals(listOf(HomeIntent.RandomEmojiClicked), intents)
    }

    @Test
    fun given_home_when_emoji_list_is_clicked_then_send_EmojiListClicked() {
        setScreen(HomeUiState.Idle)

        composeRule.onNodeWithText("EMOJI LIST").performClick()

        assertEquals(listOf(HomeIntent.EmojiListClicked), intents)
    }

    @Test
    fun given_home_when_avatar_list_is_clicked_then_send_AvatarListClicked() {
        setScreen(HomeUiState.Idle)

        composeRule.onNodeWithText("AVATAR LIST").performClick()

        assertEquals(listOf(HomeIntent.AvatarListClicked), intents)
    }

    @Test
    fun given_home_when_google_repos_is_clicked_then_send_GoogleReposClicked() {
        setScreen(HomeUiState.Idle)

        composeRule.onNodeWithText("GOOGLE REPOS").performClick()

        assertEquals(listOf(HomeIntent.GoogleReposClicked), intents)
    }

    @Test
    fun given_typed_username_when_search_is_clicked_then_send_SearchClicked_with_the_username() {
        setScreen(HomeUiState.Idle)
        composeRule.onNode(hasSetTextAction()).performTextInput("blissapps")

        composeRule.onNodeWithContentDescription("Search").performClick()

        assertEquals(listOf(HomeIntent.SearchClicked("blissapps")), intents)
    }
}
