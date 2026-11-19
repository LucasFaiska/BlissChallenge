package com.lucasoliveira.bliss.presentation.avatarlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.presentation.component.LOADING_TEST_TAG
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AvatarListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val intents = mutableListOf<AvatarListIntent>()

    private val blissAvatar = Avatar(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")
    private val googleAvatar = Avatar(login = "google", id = 1342004L, avatarUrl = "https://avatars/1342004")

    private fun setScreen(state: AvatarListUiState) {
        composeRule.setContent {
            BlissTheme {
                AvatarListScreen(uiState = state, onIntent = { intents += it })
            }
        }
    }

    @Test
    fun given_Loading_state_when_screen_is_shown_then_show_loading() {
        setScreen(AvatarListUiState.Loading)

        composeRule.onNodeWithTag(LOADING_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun given_Success_state_when_screen_is_shown_then_show_each_avatar() {
        setScreen(AvatarListUiState.Success(listOf(blissAvatar, googleAvatar)))

        composeRule.onNodeWithContentDescription("blissapps").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("google").assertIsDisplayed()
    }

    @Test
    fun given_Success_state_when_an_avatar_is_clicked_then_send_AvatarClicked_with_that_avatar() {
        setScreen(AvatarListUiState.Success(listOf(blissAvatar, googleAvatar)))

        composeRule.onNodeWithContentDescription("google").performClick()

        assertEquals(listOf(AvatarListIntent.AvatarClicked(googleAvatar)), intents)
    }

    @Test
    fun given_Success_state_with_no_avatars_when_screen_is_shown_then_show_empty_message() {
        setScreen(AvatarListUiState.Success(emptyList()))

        composeRule.onNodeWithText("No avatars yet. Search for a GitHub user on the home screen.").assertIsDisplayed()
    }

    @Test
    fun given_Error_state_when_screen_is_shown_then_show_title_and_message() {
        setScreen(AvatarListUiState.Error("Could not delete avatar"))

        composeRule.onNodeWithText("Oops!").assertIsDisplayed()
        composeRule.onNodeWithText("Could not delete avatar").assertIsDisplayed()
    }

    @Test
    fun given_Error_state_when_retry_is_clicked_then_send_LoadAvatars() {
        setScreen(AvatarListUiState.Error("Could not delete avatar"))

        composeRule.onNodeWithText("RETRY").performClick()

        assertEquals(listOf(AvatarListIntent.LoadAvatars), intents)
    }
}
