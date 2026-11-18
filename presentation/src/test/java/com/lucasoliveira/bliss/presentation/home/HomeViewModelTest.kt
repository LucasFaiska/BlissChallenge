package com.lucasoliveira.bliss.presentation.home

import app.cash.turbine.test
import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.domain.model.Emoji
import com.lucasoliveira.bliss.domain.usecase.GetAvatarUseCase
import com.lucasoliveira.bliss.domain.usecase.GetEmojisUseCase
import com.lucasoliveira.bliss.presentation.snackbar.SnackbarController
import com.lucasoliveira.bliss.presentation.snackbar.SnackbarMessage
import com.lucasoliveira.bliss.presentation.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getEmojisUseCase = mockk<GetEmojisUseCase>()
    private val getAvatarUseCase = mockk<GetAvatarUseCase>()
    private val snackbarController = mockk<SnackbarController>(relaxed = true)
    private val viewModel = HomeViewModel(getEmojisUseCase, getAvatarUseCase, snackbarController)

    private val thumbsUp = Emoji(name = "+1", url = "https://emoji/thumbs-up.png")
    private val thumbsDown = Emoji(name = "-1", url = "https://emoji/thumbs-down.png")
    private val blissAvatar = Avatar(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")

    @Test
    fun `given view model created, when no intent is sent, then state is Idle`() {
        assertEquals(HomeUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `given one emoji, when RandomEmojiClicked, then state is Success with its url`() {
        every { getEmojisUseCase() } returns flowOf(listOf(thumbsUp))

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        assertEquals(HomeUiState.Success(thumbsUp.url), viewModel.uiState.value)
    }

    @Test
    fun `given several emojis, when RandomEmojiClicked, then state is Success with one of their urls`() {
        every { getEmojisUseCase() } returns flowOf(listOf(thumbsUp, thumbsDown))

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        val state = viewModel.uiState.value as HomeUiState.Success
        assertTrue(state.imageUrl in listOf(thumbsUp.url, thumbsDown.url))
    }

    @Test
    fun `given no emojis, when RandomEmojiClicked, then state goes back to Idle`() {
        every { getEmojisUseCase() } returns flowOf(emptyList())

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        assertEquals(HomeUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `given emojis still loading, when RandomEmojiClicked, then state is Loading`() {
        val emojis = CompletableDeferred<List<Emoji>>()
        every { getEmojisUseCase() } returns flow { emit(emojis.await()) }

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
        emojis.complete(listOf(thumbsUp))
        assertEquals(HomeUiState.Success(thumbsUp.url), viewModel.uiState.value)
    }

    @Test
    fun `given use case error, when RandomEmojiClicked, then state goes back to Idle and show error snackbar`() {
        every { getEmojisUseCase() } returns flow { throw DomainException("API rate limit reached. Try again at 14:32") }

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        assertEquals(HomeUiState.Idle, viewModel.uiState.value)
        coVerify(exactly = 1) { snackbarController.show(SnackbarMessage("API rate limit reached. Try again at 14:32")) }
    }

    @Test
    fun `given emoji shown, when RandomEmojiClicked fails, then keep the previous emoji`() {
        every { getEmojisUseCase() } returns flowOf(listOf(thumbsUp))
        viewModel.onIntent(HomeIntent.RandomEmojiClicked)
        every { getEmojisUseCase() } returns flow { throw DomainException("No internet connection") }

        viewModel.onIntent(HomeIntent.RandomEmojiClicked)

        assertEquals(HomeUiState.Success(thumbsUp.url), viewModel.uiState.value)
    }

    @Test
    fun `given blank query, when SearchClicked, then avatar is not searched and state stays Idle`() {
        viewModel.onIntent(HomeIntent.SearchClicked("   "))

        verify(exactly = 0) { getAvatarUseCase(any()) }
        assertEquals(HomeUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `given query with spaces, when SearchClicked, then search trimmed login and state is Success with avatar url`() {
        every { getAvatarUseCase("blissapps") } returns flowOf(blissAvatar)

        viewModel.onIntent(HomeIntent.SearchClicked("  blissapps  "))

        verify(exactly = 1) { getAvatarUseCase("blissapps") }
        assertEquals(HomeUiState.Success(blissAvatar.avatarUrl), viewModel.uiState.value)
    }

    @Test
    fun `given avatar still loading, when SearchClicked, then state is Loading`() {
        val avatar = CompletableDeferred<Avatar>()
        every { getAvatarUseCase("blissapps") } returns flow { emit(avatar.await()) }

        viewModel.onIntent(HomeIntent.SearchClicked("blissapps"))

        assertEquals(HomeUiState.Loading, viewModel.uiState.value)
        avatar.complete(blissAvatar)
        assertEquals(HomeUiState.Success(blissAvatar.avatarUrl), viewModel.uiState.value)
    }

    @Test
    fun `given unknown user, when SearchClicked, then state goes back to Idle and show error snackbar`() {
        every { getAvatarUseCase("unknown-user") } returns flow { throw DomainException("User unknown-user not found") }

        viewModel.onIntent(HomeIntent.SearchClicked("unknown-user"))

        assertEquals(HomeUiState.Idle, viewModel.uiState.value)
        coVerify(exactly = 1) { snackbarController.show(SnackbarMessage("User unknown-user not found")) }
    }

    @Test
    fun `given emoji shown, when SearchClicked fails, then keep the previous emoji`() {
        every { getEmojisUseCase() } returns flowOf(listOf(thumbsUp))
        viewModel.onIntent(HomeIntent.RandomEmojiClicked)
        every { getAvatarUseCase("unknown-user") } returns flow { throw DomainException("User unknown-user not found") }

        viewModel.onIntent(HomeIntent.SearchClicked("unknown-user"))

        assertEquals(HomeUiState.Success(thumbsUp.url), viewModel.uiState.value)
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
