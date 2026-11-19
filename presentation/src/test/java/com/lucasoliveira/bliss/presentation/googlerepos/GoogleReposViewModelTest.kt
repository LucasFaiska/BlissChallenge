package com.lucasoliveira.bliss.presentation.googlerepos

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.lucasoliveira.bliss.domain.model.Repo
import com.lucasoliveira.bliss.domain.usecase.GetGoogleReposUseCase
import com.lucasoliveira.bliss.presentation.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GoogleReposViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getGoogleReposUseCase = mockk<GetGoogleReposUseCase>()

    private val complete = LoadState.NotLoading(endOfPaginationReached = true)

    private val completeLoadStates = LoadStates(refresh = complete, prepend = complete, append = complete)

    private val repos = listOf(
        Repo(id = 460600860L, fullName = "google/.allstar", isPrivate = false),
        Repo(id = 170908616L, fullName = "google/.github", isPrivate = false),
    )

    @Test
    fun `given google repos, when repos is collected, then emit them`() = runTest {
        every { getGoogleReposUseCase() } returns flowOf(PagingData.from(repos, completeLoadStates))

        val viewModel = GoogleReposViewModel(getGoogleReposUseCase)

        assertEquals(repos, viewModel.repos.asSnapshot())
    }

    @Test
    fun `when view model is created, then request google repos once`() {
        every { getGoogleReposUseCase() } returns flowOf(PagingData.empty())

        GoogleReposViewModel(getGoogleReposUseCase)

        verify(exactly = 1) { getGoogleReposUseCase() }
    }
}
