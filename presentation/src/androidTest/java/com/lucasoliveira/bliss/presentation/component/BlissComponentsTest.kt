package com.lucasoliveira.bliss.presentation.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BlissComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun given_action_button_when_shown_then_text_is_uppercase() {
        composeRule.setContent { BlissTheme { BlissActionButton(text = "Random emoji", onClick = {}) } }

        composeRule.onNodeWithText("RANDOM EMOJI").assertIsDisplayed()
    }

    @Test
    fun given_action_button_when_clicked_then_call_onClick() {
        var clicked = false
        composeRule.setContent { BlissTheme { BlissActionButton(text = "Emoji list", onClick = { clicked = true }) } }

        composeRule.onNodeWithText("EMOJI LIST").performClick()

        assertTrue(clicked)
    }

    @Test
    fun given_disabled_action_button_when_shown_then_it_is_not_enabled() {
        composeRule.setContent { BlissTheme { BlissActionButton(text = "Emoji list", onClick = {}, enabled = false) } }

        composeRule.onNodeWithText("EMOJI LIST").assertIsNotEnabled()
    }

    @Test
    fun given_search_field_when_text_is_typed_then_call_onQueryChange_with_the_text() {
        var typed = ""
        composeRule.setContent {
            BlissTheme {
                BlissSearchField(
                    query = "",
                    onQueryChange = { typed = it },
                    onSearch = {},
                    placeholder = "Insert github username",
                    searchContentDescription = "Search",
                )
            }
        }

        composeRule.onNode(hasSetTextAction()).performTextInput("blissapps")

        assertEquals("blissapps", typed)
    }

    @Test
    fun given_search_field_when_search_button_is_clicked_then_call_onSearch() {
        var searched = false
        composeRule.setContent {
            BlissTheme {
                BlissSearchField(
                    query = "blissapps",
                    onQueryChange = {},
                    onSearch = { searched = true },
                    placeholder = "Insert github username",
                    searchContentDescription = "Search",
                )
            }
        }

        composeRule.onNodeWithContentDescription("Search").performClick()

        assertTrue(searched)
    }

    @Test
    fun given_search_field_when_keyboard_search_action_is_used_then_call_onSearch() {
        var searched = false
        composeRule.setContent {
            BlissTheme {
                BlissSearchField(
                    query = "blissapps",
                    onQueryChange = {},
                    onSearch = { searched = true },
                    placeholder = "Insert github username",
                    searchContentDescription = "Search",
                )
            }
        }

        composeRule.onNode(hasSetTextAction()).performImeAction()

        assertTrue(searched)
    }

    @Test
    fun given_image_grid_when_an_item_is_clicked_then_call_onItemClick_with_that_item() {
        var clickedItem: String? = null
        composeRule.setContent {
            BlissTheme {
                BlissImageGrid(
                    items = listOf("first", "second"),
                    key = { it },
                    imageUrl = { "https://images/$it" },
                    contentDescription = { it },
                    onItemClick = { clickedItem = it },
                )
            }
        }

        composeRule.onNodeWithContentDescription("second").performClick()

        assertEquals("second", clickedItem)
    }

    @Test
    fun given_loading_when_shown_then_loading_tag_is_displayed() {
        composeRule.setContent { BlissTheme { BlissLoading() } }

        composeRule.onNodeWithTag(LOADING_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun given_empty_message_when_shown_then_message_is_displayed() {
        composeRule.setContent { BlissTheme { BlissEmptyMessage(message = "No avatars yet") } }

        composeRule.onNodeWithText("No avatars yet").assertIsDisplayed()
    }

    @Test
    fun given_error_view_when_retry_is_clicked_then_call_onRetry() {
        var retried = false
        composeRule.setContent {
            BlissTheme {
                BlissErrorView(title = "Oops!", message = "No internet connection", retryLabel = "Retry", onRetry = { retried = true })
            }
        }

        composeRule.onNodeWithText("Oops!").assertIsDisplayed()
        composeRule.onNodeWithText("No internet connection").assertIsDisplayed()
        composeRule.onNodeWithText("RETRY").performClick()

        assertTrue(retried)
    }

    @Test
    fun given_image_that_fails_to_load_when_shown_then_show_broken_image_keeping_the_description() {
        composeRule.setContent {
            BlissTheme {
                BlissImage(url = "https://images/unreachable.png", contentDescription = "+1")
            }
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag(BROKEN_IMAGE_TEST_TAG).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag(BROKEN_IMAGE_TEST_TAG).assertIsDisplayed()
        composeRule.onNodeWithContentDescription("+1").assertIsDisplayed()
    }
}
