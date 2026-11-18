package com.lucasoliveira.bliss.presentation.component

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class BlissComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `given action button, when shown, then text is uppercase`() {
        composeRule.setContent { BlissTheme { BlissActionButton(text = "Random emoji", onClick = {}) } }

        composeRule.onNodeWithText("RANDOM EMOJI").assertIsDisplayed()
    }

    @Test
    fun `given action button, when clicked, then call onClick`() {
        var clicked = false
        composeRule.setContent { BlissTheme { BlissActionButton(text = "Emoji list", onClick = { clicked = true }) } }

        composeRule.onNodeWithText("EMOJI LIST").performClick()

        assertTrue(clicked)
    }

    @Test
    fun `given disabled action button, when shown, then it is not enabled`() {
        composeRule.setContent { BlissTheme { BlissActionButton(text = "Emoji list", onClick = {}, enabled = false) } }

        composeRule.onNodeWithText("EMOJI LIST").assertIsNotEnabled()
    }

    @Test
    fun `given search field, when text is typed, then call onQueryChange with the text`() {
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
    fun `given search field, when search button is clicked, then call onSearch`() {
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
    fun `given search field, when keyboard search action is used, then call onSearch`() {
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
    fun `given image grid, when an item is clicked, then call onItemClick with that item`() {
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
    fun `given loading, when shown, then loading tag is displayed`() {
        composeRule.setContent { BlissTheme { BlissLoading() } }

        composeRule.onNodeWithTag(LOADING_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun `given empty message, when shown, then message is displayed`() {
        composeRule.setContent { BlissTheme { BlissEmptyMessage(message = "No avatars yet") } }

        composeRule.onNodeWithText("No avatars yet").assertIsDisplayed()
    }

    @Test
    fun `given error view, when retry is clicked, then call onRetry`() {
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
}
