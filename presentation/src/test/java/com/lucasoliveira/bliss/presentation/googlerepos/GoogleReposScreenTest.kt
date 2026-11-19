package com.lucasoliveira.bliss.presentation.googlerepos

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.lucasoliveira.bliss.domain.model.Repo
import com.lucasoliveira.bliss.presentation.component.LOADING_TEST_TAG
import com.lucasoliveira.bliss.presentation.theme.BlissTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class GoogleReposScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val notLoading = LoadState.NotLoading(endOfPaginationReached = false)

    private val repos = listOf(
        Repo(id = 460600860L, fullName = "google/.allstar", isPrivate = false),
        Repo(id = 170908616L, fullName = "google/.github", isPrivate = false),
    )

    private fun setScreen(data: List<Repo>, refresh: LoadState, append: LoadState = notLoading) {
        composeRule.setContent {
            BlissTheme {
                val items = flowOf(
                    PagingData.from(data, LoadStates(refresh = refresh, prepend = notLoading, append = append))
                ).collectAsLazyPagingItems()
                GoogleReposScreen(repos = items)
            }
        }
    }

    @Test
    fun `given loaded repos, when screen is shown, then show each repo full name`() {
        setScreen(repos, refresh = notLoading)

        composeRule.onNodeWithText("google/.allstar").assertIsDisplayed()
        composeRule.onNodeWithText("google/.github").assertIsDisplayed()
    }

    @Test
    fun `given first page loading, when screen is shown, then show loading`() {
        setScreen(emptyList(), refresh = LoadState.Loading)

        composeRule.onNodeWithTag(LOADING_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun `given first page error, when screen is shown, then show error message and retry`() {
        setScreen(emptyList(), refresh = LoadState.Error(IllegalStateException("API rate limit reached. Try again at 14:32")))

        composeRule.onNodeWithText("Oops!").assertIsDisplayed()
        composeRule.onNodeWithText("API rate limit reached. Try again at 14:32").assertIsDisplayed()
        composeRule.onNodeWithText("RETRY").assertIsDisplayed()
    }

    @Test
    fun `given no repos and end of pagination, when screen is shown, then show empty message`() {
        setScreen(emptyList(), refresh = LoadState.NotLoading(endOfPaginationReached = true))

        composeRule.onNodeWithText("No repositories found.").assertIsDisplayed()
    }

    @Test
    fun `given next page loading, when screen is shown, then show repos and loading footer`() {
        setScreen(repos, refresh = notLoading, append = LoadState.Loading)

        composeRule.onNodeWithText("google/.github").assertIsDisplayed()
        composeRule.onNodeWithTag(LOADING_TEST_TAG).assertIsDisplayed()
    }

    @Test
    fun `given next page error, when screen is shown, then show repos and footer with retry`() {
        setScreen(repos, refresh = notLoading, append = LoadState.Error(IllegalStateException("No internet connection")))

        composeRule.onNodeWithText("google/.allstar").assertIsDisplayed()
        composeRule.onNodeWithText("No internet connection").assertIsDisplayed()
        composeRule.onNodeWithText("RETRY").assertIsDisplayed()
    }
}
