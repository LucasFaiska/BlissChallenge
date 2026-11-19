package com.lucasoliveira.bliss.domain.usecase

import androidx.paging.PagingData
import com.lucasoliveira.bliss.domain.model.Repo
import com.lucasoliveira.bliss.domain.repository.RepoRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertSame
import org.junit.Test

class GetGoogleReposUseCaseTest {

    private val repoRepository = mockk<RepoRepository>()
    private val getGoogleReposUseCase = GetGoogleReposUseCase(repoRepository)

    @Test
    fun `when invoked, then request repos of the google user`() {
        every { repoRepository.getUserRepos(any()) } returns flowOf(PagingData.empty())

        getGoogleReposUseCase()

        verify(exactly = 1) { repoRepository.getUserRepos("google") }
    }

    @Test
    fun `given repository paging flow, when invoked, then return the same flow`() {
        val pagingFlow: Flow<PagingData<Repo>> = flowOf(PagingData.empty())
        every { repoRepository.getUserRepos("google") } returns pagingFlow

        assertSame(pagingFlow, getGoogleReposUseCase())
    }
}
