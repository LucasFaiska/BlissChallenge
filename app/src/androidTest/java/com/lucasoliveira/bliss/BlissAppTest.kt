package com.lucasoliveira.bliss

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class BlissAppTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    private fun waitForText(text: String) {
        composeRule.waitUntil(TIMEOUT_MILLIS) {
            composeRule.onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForContentDescription(description: String) {
        composeRule.waitUntil(TIMEOUT_MILLIS) {
            composeRule.onAllNodes(hasContentDescription(description))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun searchUser(username: String) {
        composeRule.onNode(hasSetTextAction()).performTextInput(username)
        composeRule.onNodeWithContentDescription("Search").performClick()
    }

    @Test
    fun given_app_launched_when_home_is_shown_then_show_default_image_and_all_actions() {
        composeRule.onNodeWithContentDescription("Default image").assertIsDisplayed()
        composeRule.onNodeWithText("RANDOM EMOJI").assertIsDisplayed()
        composeRule.onNodeWithText("EMOJI LIST").assertIsDisplayed()
        composeRule.onNodeWithText("AVATAR LIST").assertIsDisplayed()
        composeRule.onNodeWithText("GOOGLE REPOS").assertIsDisplayed()
    }

    @Test
    fun given_home_when_random_emoji_is_clicked_then_replace_default_image_with_selected_image() {
        composeRule.onNodeWithText("RANDOM EMOJI").performClick()

        waitForContentDescription("Selected image")
        composeRule.onNodeWithContentDescription("Default image").assertDoesNotExist()
    }

    @Test
    fun given_home_when_emoji_list_is_clicked_then_show_all_emojis() {
        composeRule.onNodeWithText("EMOJI LIST").performClick()

        waitForContentDescription("rocket")
        composeRule.onNodeWithContentDescription("+1").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("tada").assertIsDisplayed()
    }

    @Test
    fun given_emoji_list_when_emoji_is_clicked_then_remove_it_from_the_list() {
        composeRule.onNodeWithText("EMOJI LIST").performClick()
        waitForContentDescription("rocket")

        composeRule.onNodeWithContentDescription("rocket").performClick()

        composeRule.onNodeWithContentDescription("rocket").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("tada").assertIsDisplayed()
    }

    @Test
    fun given_emoji_list_when_back_is_pressed_then_return_to_home() {
        composeRule.onNodeWithText("EMOJI LIST").performClick()
        waitForContentDescription("rocket")

        composeRule.runOnUiThread { composeRule.activity.onBackPressedDispatcher.onBackPressed() }

        composeRule.onNodeWithText("RANDOM EMOJI").assertIsDisplayed()
    }

    @Test
    fun given_existing_user_when_search_is_clicked_then_show_avatar_and_save_it_in_avatar_list() {
        searchUser("blissapps")
        waitForContentDescription("Selected image")

        composeRule.onNodeWithText("AVATAR LIST").performClick()

        waitForContentDescription("blissapps")
    }

    @Test
    fun given_unknown_user_when_search_is_clicked_then_show_snackbar_and_keep_default_image() {
        searchUser("ghost-user-404")

        waitForText("User ghost-user-404 not found")
        composeRule.onNodeWithContentDescription("Default image").assertIsDisplayed()
    }

    @Test
    fun given_no_saved_avatars_when_avatar_list_is_clicked_then_show_empty_message() {
        composeRule.onNodeWithText("AVATAR LIST").performClick()

        waitForText("No avatars yet. Search for a GitHub user on the home screen.")
    }

    @Test
    fun given_saved_avatars_when_avatar_is_clicked_then_delete_it_from_the_list() {
        searchUser("blissapps")
        waitForContentDescription("Selected image")
        composeRule.onNodeWithText("AVATAR LIST").performClick()
        waitForContentDescription("blissapps")

        composeRule.onNodeWithContentDescription("blissapps").performClick()

        waitForText("No avatars yet. Search for a GitHub user on the home screen.")
    }

    @Test
    fun given_home_when_google_repos_is_clicked_then_show_google_repositories() {
        composeRule.onNodeWithText("GOOGLE REPOS").performClick()

        waitForText("google/.allstar")
        composeRule.onNodeWithText("google/.github").assertIsDisplayed()
    }

    private companion object {
        const val TIMEOUT_MILLIS = 5_000L
    }
}
