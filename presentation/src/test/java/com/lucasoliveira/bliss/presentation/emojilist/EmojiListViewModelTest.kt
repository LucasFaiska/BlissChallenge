package com.lucasoliveira.bliss.presentation.emojilist

import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.model.Emoji
import com.lucasoliveira.bliss.domain.usecase.GetEmojisUseCase
import com.lucasoliveira.bliss.presentation.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class EmojiListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getEmojisUseCase = mockk<GetEmojisUseCase>()
    private val viewModel = EmojiListViewModel(getEmojisUseCase)

    private val thumbsUp = Emoji(name = "+1", url = "https://emoji/thumbs-up.png")
    private val thumbsDown = Emoji(name = "-1", url = "https://emoji/thumbs-down.png")
    private val hundred = Emoji(name = "100", url = "https://emoji/100.png")
    private val allEmojis = listOf(thumbsUp, thumbsDown, hundred)

    @Test
    fun `given initial state, when view model is created, then state is empty`() {
        assertEquals(EmojiListState(), viewModel.uiState.value)
    }

    @Test
    fun `given emojis, when LoadEmojis, then show all emojis and stop loading`() {
        every { getEmojisUseCase() } returns flowOf(allEmojis)

        viewModel.onIntent(EmojiListIntent.LoadEmojis)

        assertEquals(allEmojis, viewModel.uiState.value.emojis)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given emojis still loading, when LoadEmojis, then state is loading and not refreshing`() {
        val emojis = CompletableDeferred<List<Emoji>>()
        every { getEmojisUseCase() } returns flow { emit(emojis.await()) }

        viewModel.onIntent(EmojiListIntent.LoadEmojis)

        assertTrue(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isRefreshing)
        emojis.complete(allEmojis)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given use case error, when LoadEmojis, then show error message and stop loading`() {
        every { getEmojisUseCase() } returns flow { throw DomainException("No internet connection") }

        viewModel.onIntent(EmojiListIntent.LoadEmojis)

        assertEquals("No internet connection", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given loaded emojis, when EmojiClicked, then remove only that emoji from the list`() {
        every { getEmojisUseCase() } returns flowOf(allEmojis)
        viewModel.onIntent(EmojiListIntent.LoadEmojis)

        viewModel.onIntent(EmojiListIntent.EmojiClicked(thumbsDown))

        assertEquals(listOf(thumbsUp, hundred), viewModel.uiState.value.emojis)
    }

    @Test
    fun `given loaded emojis, when EmojiClicked, then use case is not called again`() {
        every { getEmojisUseCase() } returns flowOf(allEmojis)
        viewModel.onIntent(EmojiListIntent.LoadEmojis)

        viewModel.onIntent(EmojiListIntent.EmojiClicked(thumbsUp))

        verify(exactly = 1) { getEmojisUseCase() }
    }

    @Test
    fun `given removed emojis, when Refresh, then restore the full list`() {
        every { getEmojisUseCase() } returns flowOf(allEmojis)
        viewModel.onIntent(EmojiListIntent.LoadEmojis)
        viewModel.onIntent(EmojiListIntent.EmojiClicked(thumbsUp))
        viewModel.onIntent(EmojiListIntent.EmojiClicked(hundred))

        viewModel.onIntent(EmojiListIntent.Refresh)

        assertEquals(allEmojis, viewModel.uiState.value.emojis)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun `given refresh in progress, when Refresh, then state is refreshing and not loading`() {
        val emojis = CompletableDeferred<List<Emoji>>()
        every { getEmojisUseCase() } returns flow { emit(emojis.await()) }

        viewModel.onIntent(EmojiListIntent.Refresh)

        assertTrue(viewModel.uiState.value.isRefreshing)
        assertFalse(viewModel.uiState.value.isLoading)
        emojis.complete(allEmojis)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun `given use case error, when Refresh, then show error message and stop refreshing`() {
        every { getEmojisUseCase() } returns flow { throw DomainException("Something went wrong") }

        viewModel.onIntent(EmojiListIntent.Refresh)

        assertEquals("Something went wrong", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun `given previous error, when Refresh succeeds, then error is cleared`() {
        every { getEmojisUseCase() } returns flow { throw DomainException("No internet connection") }
        viewModel.onIntent(EmojiListIntent.LoadEmojis)
        every { getEmojisUseCase() } returns flowOf(allEmojis)

        viewModel.onIntent(EmojiListIntent.Refresh)

        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(allEmojis, viewModel.uiState.value.emojis)
    }
}
