package com.lucasoliveira.bliss.presentation.avatarlist

import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.domain.usecase.DeleteAvatarUseCase
import com.lucasoliveira.bliss.domain.usecase.GetAvatarsUseCase
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

class AvatarListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getAvatarsUseCase = mockk<GetAvatarsUseCase>()
    private val deleteAvatarUseCase = mockk<DeleteAvatarUseCase>()
    private val snackbarController = mockk<SnackbarController>(relaxed = true)
    private val viewModel = AvatarListViewModel(getAvatarsUseCase, deleteAvatarUseCase, snackbarController)

    private val blissAvatar = Avatar(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")
    private val googleAvatar = Avatar(login = "google", id = 1342004L, avatarUrl = "https://avatars/1342004")

    @Test
    fun `given view model created, when no intent is sent, then state is Loading`() {
        assertEquals(AvatarListUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `given saved avatars, when LoadAvatars, then state is Success with them`() {
        every { getAvatarsUseCase() } returns flowOf(listOf(blissAvatar, googleAvatar))

        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        assertEquals(AvatarListUiState.Success(listOf(blissAvatar, googleAvatar)), viewModel.uiState.value)
    }

    @Test
    fun `given no saved avatars, when LoadAvatars, then state is Success with empty list`() {
        every { getAvatarsUseCase() } returns flowOf(emptyList())

        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        assertEquals(AvatarListUiState.Success(emptyList()), viewModel.uiState.value)
    }

    @Test
    fun `given avatars still loading, when LoadAvatars, then state is Loading`() {
        val avatars = CompletableDeferred<List<Avatar>>()
        every { getAvatarsUseCase() } returns flow { emit(avatars.await()) }

        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        assertEquals(AvatarListUiState.Loading, viewModel.uiState.value)
        avatars.complete(listOf(blissAvatar))
        assertEquals(AvatarListUiState.Success(listOf(blissAvatar)), viewModel.uiState.value)
    }

    @Test
    fun `given use case error, when LoadAvatars, then state is Error with the message`() {
        every { getAvatarsUseCase() } returns flow { throw DomainException("Something went wrong") }

        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        assertEquals(AvatarListUiState.Error("Something went wrong"), viewModel.uiState.value)
    }

    @Test
    fun `given Success state, when AvatarClicked, then delete it by login and remove it from the list`() {
        every { getAvatarsUseCase() } returns flowOf(listOf(blissAvatar, googleAvatar))
        every { deleteAvatarUseCase("blissapps") } returns flowOf(Unit)
        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        viewModel.onIntent(AvatarListIntent.AvatarClicked(blissAvatar))

        verify(exactly = 1) { deleteAvatarUseCase("blissapps") }
        assertEquals(AvatarListUiState.Success(listOf(googleAvatar)), viewModel.uiState.value)
    }

    @Test
    fun `given delete failure, when AvatarClicked, then keep the avatar and show error snackbar`() {
        every { getAvatarsUseCase() } returns flowOf(listOf(blissAvatar, googleAvatar))
        every { deleteAvatarUseCase("blissapps") } returns flow { throw DomainException("Could not delete avatar") }
        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        viewModel.onIntent(AvatarListIntent.AvatarClicked(blissAvatar))

        assertEquals(AvatarListUiState.Success(listOf(blissAvatar, googleAvatar)), viewModel.uiState.value)
        coVerify(exactly = 1) { snackbarController.show(SnackbarMessage("Could not delete avatar")) }
    }

    @Test
    fun `given Error state, when LoadAvatars succeeds, then state is Success`() {
        every { getAvatarsUseCase() } returns flow { throw DomainException("Something went wrong") }
        viewModel.onIntent(AvatarListIntent.LoadAvatars)
        every { getAvatarsUseCase() } returns flowOf(listOf(blissAvatar))

        viewModel.onIntent(AvatarListIntent.LoadAvatars)

        assertEquals(AvatarListUiState.Success(listOf(blissAvatar)), viewModel.uiState.value)
    }
}
