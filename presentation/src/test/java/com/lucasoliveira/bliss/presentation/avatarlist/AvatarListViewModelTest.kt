package com.lucasoliveira.bliss.presentation.avatarlist

import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.domain.usecase.DeleteAvatarUseCase
import com.lucasoliveira.bliss.domain.usecase.GetAvatarsUseCase
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

class AvatarListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getAvatarsUseCase = mockk<GetAvatarsUseCase>()
    private val deleteAvatarUseCase = mockk<DeleteAvatarUseCase>()
    private val viewModel = AvatarListViewModel(getAvatarsUseCase, deleteAvatarUseCase)

    private val blissAvatar = Avatar(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")
    private val googleAvatar = Avatar(login = "google", id = 1342004L, avatarUrl = "https://avatars/1342004")

    @Test
    fun `given initial state, when view model is created, then state is empty`() {
        assertEquals(AvatarListState(), viewModel.uiState.value)
    }

    @Test
    fun `given saved avatars, when LoadAvatars, then show them and stop loading`() {
        every { getAvatarsUseCase() } returns flowOf(listOf(blissAvatar, googleAvatar))

        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        assertEquals(listOf(blissAvatar, googleAvatar), viewModel.uiState.value.avatars)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given avatars still loading, when LoadAvatars, then state is loading`() {
        val avatars = CompletableDeferred<List<Avatar>>()
        every { getAvatarsUseCase() } returns flow { emit(avatars.await()) }

        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        assertTrue(viewModel.uiState.value.isLoading)
        avatars.complete(listOf(blissAvatar))
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given use case error, when LoadAvatars, then show error message and stop loading`() {
        every { getAvatarsUseCase() } returns flow { throw DomainException("Something went wrong") }

        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        assertEquals("Something went wrong", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `given loaded avatars, when AvatarClicked, then delete it by login and remove it from the list`() {
        every { getAvatarsUseCase() } returns flowOf(listOf(blissAvatar, googleAvatar))
        every { deleteAvatarUseCase("blissapps") } returns flowOf(Unit)
        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        viewModel.onIntent(AvatarListIntent.AvatarClicked(blissAvatar))

        verify(exactly = 1) { deleteAvatarUseCase("blissapps") }
        assertEquals(listOf(googleAvatar), viewModel.uiState.value.avatars)
    }

    @Test
    fun `given delete failure, when AvatarClicked, then keep the avatar and show error message`() {
        every { getAvatarsUseCase() } returns flowOf(listOf(blissAvatar, googleAvatar))
        every { deleteAvatarUseCase("blissapps") } returns flow { throw DomainException("Could not delete avatar") }
        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        viewModel.onIntent(AvatarListIntent.AvatarClicked(blissAvatar))

        assertEquals(listOf(blissAvatar, googleAvatar), viewModel.uiState.value.avatars)
        assertEquals("Could not delete avatar", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `given error shown, when ErrorDismissed, then error is cleared`() {
        every { getAvatarsUseCase() } returns flow { throw DomainException("Something went wrong") }
        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        viewModel.onIntent(AvatarListIntent.ErrorDismissed)

        assertNull(viewModel.uiState.value.errorMessage)
    }
}
