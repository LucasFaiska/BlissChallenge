package com.lucasoliveira.bliss.presentation.avatarlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.lucasoliveira.bliss.domain.model.Avatar
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
    fun `given Loading state, when screen is shown, then show loading`() {
        setScreen(AvatarListUiState.Loading)

        composeRule.onNodeWithTag(LOADING_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun `given Success state, when screen is shown, then show each avatar`() {
        setScreen(AvatarListUiState.Success(listOf(blissAvatar, googleAvatar)))

        composeRule.onNodeWithContentDescription("blissapps").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("google").assertIsDisplayed()
    }

    @Test
    fun `given Success state, when an avatar is clicked, then send AvatarClicked with that avatar`() {
        setScreen(AvatarListUiState.Success(listOf(blissAvatar, googleAvatar)))

        composeRule.onNodeWithContentDescription("google").performClick()

        assertEquals(listOf(AvatarListIntent.AvatarClicked(googleAvatar)), intents)
    }

    @Test
    fun `given Success state with no avatars, when screen is shown, then show empty message`() {
        setScreen(AvatarListUiState.Success(emptyList()))

        composeRule.onNodeWithText("No avatars yet. Search for a GitHub user on the home screen.").assertIsDisplayed()
    }

    @Test
    fun `given Error state, when screen is shown, then show title and message`() {
        setScreen(AvatarListUiState.Error("Could not delete avatar"))

        composeRule.onNodeWithText("Oops!").assertIsDisplayed()
        composeRule.onNodeWithText("Could not delete avatar").assertIsDisplayed()
    }

    @Test
    fun `given Error state, when retry is clicked, then send LoadAvatars`() {
        setScreen(AvatarListUiState.Error("Could not delete avatar"))

        composeRule.onNodeWithText("RETRY").performClick()

        assertEquals(listOf(AvatarListIntent.LoadAvatars), intents)
    }
}
