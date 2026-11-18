package com.lucasoliveira.bliss.presentation.emojilist

import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.model.Emoji
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
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EmojiListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getEmojisUseCase = mockk<GetEmojisUseCase>()
    private val snackbarController = mockk<SnackbarController>(relaxed = true)
    private val viewModel = EmojiListViewModel(getEmojisUseCase, snackbarController)

    private val thumbsUp = Emoji(name = "+1", url = "https://emoji/thumbs-up.png")
    private val thumbsDown = Emoji(name = "-1", url = "https://emoji/thumbs-down.png")
    private val hundred = Emoji(name = "100", url = "https://emoji/100.png")
    private val allEmojis = listOf(thumbsUp, thumbsDown, hundred)

    @Test
    fun `given view model created, when no intent is sent, then state is Loading`() {
        assertEquals(EmojiListUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `given emojis, when LoadEmojis, then state is Success with all emojis`() {
        every { getEmojisUseCase() } returns flowOf(allEmojis)

        viewModel.onIntent(EmojiListIntent.LoadEmojis)

        assertEquals(EmojiListUiState.Success(allEmojis), viewModel.uiState.value)
    }

    @Test
    fun `given emojis still loading, when LoadEmojis, then state is Loading`() {
        val emojis = CompletableDeferred<List<Emoji>>()
        every { getEmojisUseCase() } returns flow { emit(emojis.await()) }

        viewModel.onIntent(EmojiListIntent.LoadEmojis)

        assertEquals(EmojiListUiState.Loading, viewModel.uiState.value)
        emojis.complete(allEmojis)
        assertEquals(EmojiListUiState.Success(allEmojis), viewModel.uiState.value)
    }

    @Test
    fun `given use case error, when LoadEmojis, then state is Error and no snackbar is shown`() {
        every { getEmojisUseCase() } returns flow { throw DomainException("No internet connection") }

        viewModel.onIntent(EmojiListIntent.LoadEmojis)

        assertEquals(EmojiListUiState.Error("No internet connection"), viewModel.uiState.value)
        coVerify(exactly = 0) { snackbarController.show(any()) }
    }

    @Test
    fun `given Success state, when EmojiClicked, then remove only that emoji from memory`() {
        every { getEmojisUseCase() } returns flowOf(allEmojis)
        viewModel.onIntent(EmojiListIntent.LoadEmojis)

        viewModel.onIntent(EmojiListIntent.EmojiClicked(thumbsDown))

        assertEquals(EmojiListUiState.Success(listOf(thumbsUp, hundred)), viewModel.uiState.value)
        verify(exactly = 1) { getEmojisUseCase() }
    }

    @Test
    fun `given Loading state, when EmojiClicked, then state does not change`() {
        viewModel.onIntent(EmojiListIntent.EmojiClicked(thumbsUp))

        assertEquals(EmojiListUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `given removed emojis, when Refresh, then state is Success with the full list`() {
        every { getEmojisUseCase() } returns flowOf(allEmojis)
        viewModel.onIntent(EmojiListIntent.LoadEmojis)
        viewModel.onIntent(EmojiListIntent.EmojiClicked(thumbsUp))
        viewModel.onIntent(EmojiListIntent.EmojiClicked(hundred))

        viewModel.onIntent(EmojiListIntent.Refresh)

        assertEquals(EmojiListUiState.Success(allEmojis), viewModel.uiState.value)
    }

    @Test
    fun `given Success state and refresh in progress, when Refresh, then state keeps emojis and is refreshing`() {
        every { getEmojisUseCase() } returns flowOf(allEmojis)
        viewModel.onIntent(EmojiListIntent.LoadEmojis)
        viewModel.onIntent(EmojiListIntent.EmojiClicked(thumbsUp))
        val refreshed = CompletableDeferred<List<Emoji>>()
        every { getEmojisUseCase() } returns flow { emit(refreshed.await()) }

        viewModel.onIntent(EmojiListIntent.Refresh)

        assertEquals(
            EmojiListUiState.Success(listOf(thumbsDown, hundred), isRefreshing = true),
            viewModel.uiState.value
        )
        refreshed.complete(allEmojis)
        assertEquals(EmojiListUiState.Success(allEmojis), viewModel.uiState.value)
    }

    @Test
    fun `given Error state, when Refresh, then state is Loading until emojis arrive`() {
        every { getEmojisUseCase() } returns flow { throw DomainException("No internet connection") }
        viewModel.onIntent(EmojiListIntent.LoadEmojis)
        val emojis = CompletableDeferred<List<Emoji>>()
        every { getEmojisUseCase() } returns flow { emit(emojis.await()) }

        viewModel.onIntent(EmojiListIntent.Refresh)

        assertEquals(EmojiListUiState.Loading, viewModel.uiState.value)
        emojis.complete(allEmojis)
        assertEquals(EmojiListUiState.Success(allEmojis), viewModel.uiState.value)
    }

    @Test
    fun `given Success state, when Refresh fails, then keep emojis, stop refreshing and show error snackbar`() {
        every { getEmojisUseCase() } returns flowOf(allEmojis)
        viewModel.onIntent(EmojiListIntent.LoadEmojis)
        viewModel.onIntent(EmojiListIntent.EmojiClicked(thumbsUp))
        every { getEmojisUseCase() } returns flow { throw DomainException("Something went wrong") }

        viewModel.onIntent(EmojiListIntent.Refresh)

        assertEquals(EmojiListUiState.Success(listOf(thumbsDown, hundred)), viewModel.uiState.value)
        coVerify(exactly = 1) { snackbarController.show(SnackbarMessage("Something went wrong")) }
    }

    @Test
    fun `given Error state, when Refresh fails, then state is Error with the new message`() {
        every { getEmojisUseCase() } returns flow { throw DomainException("No internet connection") }
        viewModel.onIntent(EmojiListIntent.LoadEmojis)
        every { getEmojisUseCase() } returns flow { throw DomainException("Something went wrong") }

        viewModel.onIntent(EmojiListIntent.Refresh)

        assertEquals(EmojiListUiState.Error("Something went wrong"), viewModel.uiState.value)
    }
}
