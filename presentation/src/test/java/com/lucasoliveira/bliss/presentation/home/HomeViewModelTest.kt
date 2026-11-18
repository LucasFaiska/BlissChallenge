package com.lucasoliveira.bliss.presentation.home

import app.cash.turbine.test
import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.domain.model.Emoji
import com.lucasoliveira.bliss.domain.usecase.GetAvatarUseCase
import com.lucasoliveira.bliss.domain.usecase.GetEmojisUseCase
import com.lucasoliveira.bliss.presentation.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getEmojisUseCase = mockk<GetEmojisUseCase>()
    private val getAvatarUseCase = mockk<GetAvatarUseCase>()
    private val viewModel = HomeViewModel(getEmojisUseCase, getAvatarUseCase)

    private val thumbsUp = Emoji(name = "+1", url = "https://emoji/thumbs-up.png")
    private val thumbsDown = Emoji(name = "-1", url = "https://emoji/thumbs-down.png")
    private val blissAvatar = Avatar(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")

    @Test
    fun `given initial state, when view model is created, then state is empty`() {
        assertEquals(HomeState(), viewModel.uiState.value)
    }

    @Test
    fun `given one emoji, when RandomEmojiClicked, then show its url and stop loading`() {
        every { getEmojisUseCase() } returns flowOf(listOf(thumbsUp))

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        assertEquals("https://emoji/thumbs-up.png", viewModel.uiState.value.imageUrl)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given several emojis, when RandomEmojiClicked, then show the url of one of them`() {
        every { getEmojisUseCase() } returns flowOf(listOf(thumbsUp, thumbsDown))

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        assertTrue(viewModel.uiState.value.imageUrl in listOf(thumbsUp.url, thumbsDown.url))
    }

    @Test
    fun `given no emojis, when RandomEmojiClicked, then image stays empty`() {
        every { getEmojisUseCase() } returns flowOf(emptyList())

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        assertNull(viewModel.uiState.value.imageUrl)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given emojis still loading, when RandomEmojiClicked, then state is loading`() {
        val emojis = CompletableDeferred<List<Emoji>>()
        every { getEmojisUseCase() } returns flow { emit(emojis.await()) }

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        assertTrue(viewModel.uiState.value.isLoading)
        emojis.complete(listOf(thumbsUp))
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given use case error, when RandomEmojiClicked, then show error message and stop loading`() {
        every { getEmojisUseCase() } returns flow { throw DomainException("API rate limit reached. Try again at 14:32") }

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        assertEquals("API rate limit reached. Try again at 14:32", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given previous error, when RandomEmojiClicked succeeds, then error is cleared`() {
        every { getEmojisUseCase() } returns flow { throw DomainException("No internet connection") }
        viewModel.onIntent(HomeIntent.RandomEmojiClicked)
        every { getEmojisUseCase() } returns flowOf(listOf(thumbsUp))

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(thumbsUp.url, viewModel.uiState.value.imageUrl)
    }

    @Test
    fun `given typed text, when QueryChanged, then query is updated`() {
        viewModel.onIntent(HomeIntent.QueryChanged("blissapps"))

        assertEquals("blissapps", viewModel.uiState.value.query)
    }

    @Test
    fun `given blank query, when SearchClicked, then avatar is not searched`() {
        viewModel.onIntent(HomeIntent.QueryChanged("   "))

        viewModel.onIntent(HomeIntent.SearchClicked)

        verify(exactly = 0) { getAvatarUseCase(any()) }
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given query with spaces, when SearchClicked, then search trimmed login and show avatar url`() {
        every { getAvatarUseCase("blissapps") } returns flowOf(blissAvatar)
        viewModel.onIntent(HomeIntent.QueryChanged("  blissapps  "))

        viewModel.onIntent(HomeIntent.SearchClicked)

        verify(exactly = 1) { getAvatarUseCase("blissapps") }
        assertEquals("https://avatars/223156", viewModel.uiState.value.imageUrl)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given avatar still loading, when SearchClicked, then state is loading`() {
        val avatar = CompletableDeferred<Avatar>()
        every { getAvatarUseCase("blissapps") } returns flow { emit(avatar.await()) }
        viewModel.onIntent(HomeIntent.QueryChanged("blissapps"))

        viewModel.onIntent(HomeIntent.SearchClicked)

        assertTrue(viewModel.uiState.value.isLoading)
        avatar.complete(blissAvatar)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given unknown user, when SearchClicked, then show error message and keep previous image`() {
        every { getEmojisUseCase() } returns flowOf(listOf(thumbsUp))
        viewModel.onIntent(HomeIntent.RandomEmojiClicked)
        every { getAvatarUseCase("unknown-user") } returns flow { throw DomainException("User unknown-user not found") }
        viewModel.onIntent(HomeIntent.QueryChanged("unknown-user"))

        viewModel.onIntent(HomeIntent.SearchClicked)

        assertEquals("User unknown-user not found", viewModel.uiState.value.errorMessage)
        assertEquals(thumbsUp.url, viewModel.uiState.value.imageUrl)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given error shown, when ErrorDismissed, then error is cleared`() {
        every { getEmojisUseCase() } returns flow { throw DomainException("No internet connection") }
        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        viewModel.onIntent(HomeIntent.ErrorDismissed)

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `given home, when EmojiListClicked, then navigate to emoji list`() = runTest {
        viewModel.sideEffect.test {
            viewModel.onIntent(HomeIntent.EmojiListClicked)

            assertEquals(HomeSideEffect.NavigateToEmojiList, awaitItem())
        }
    }

    @Test
    fun `given home, when AvatarListClicked, then navigate to avatar list`() = runTest {
        viewModel.sideEffect.test {
            viewModel.onIntent(HomeIntent.AvatarListClicked)

            assertEquals(HomeSideEffect.NavigateToAvatarList, awaitItem())
        }
    }

    @Test
    fun `given home, when GoogleReposClicked, then navigate to google repos`() = runTest {
        viewModel.sideEffect.test {
            viewModel.onIntent(HomeIntent.GoogleReposClicked)

            assertEquals(HomeSideEffect.NavigateToGoogleRepos, awaitItem())
        }
    }
}
