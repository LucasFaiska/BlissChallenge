package com.lucasoliveira.bliss.data.repository

import androidx.paging.testing.asSnapshot
import com.lucasoliveira.bliss.data.remote.RemoteDataSource
import com.lucasoliveira.bliss.data.remote.dto.RepoResponseDto
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import com.lucasoliveira.bliss.domain.model.Repo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RepoRepositoryImplTest {

    private val remoteDataSource = mockk<RemoteDataSource>()
    private val repoRepository = RepoRepositoryImpl(remoteDataSource)

    @Test
    fun `given one page of repos, when getUserRepos is collected, then emit repos as domain models`() = runTest {
        coEvery { remoteDataSource.getUserRepos("google", 1, 100) } returns GithubApiResponse.Success(
            listOf(
                RepoResponseDto(id = 460600860L, fullName = "google/.allstar", isPrivate = false),
                RepoResponseDto(id = 170908616L, fullName = "google/.github", isPrivate = false),
            )
        )

        val repos = repoRepository.getUserRepos("google").asSnapshot()

        assertEquals(
            listOf(
                Repo(id = 460600860L, fullName = "google/.allstar", isPrivate = false),
                Repo(id = 170908616L, fullName = "google/.github", isPrivate = false),
            ),
            repos
        )
    }

    @Test
    fun `given full first page, when collected, then request the first page with page size 100`() = runTest {
        val fullPage = (1L..100L).map { RepoResponseDto(id = it, fullName = "google/repo-$it", isPrivate = false) }
        coEvery { remoteDataSource.getUserRepos("google", 1, 100) } returns GithubApiResponse.Success(fullPage)
        coEvery { remoteDataSource.getUserRepos("google", 2, 100) } returns GithubApiResponse.Success(emptyList())

        val repos = repoRepository.getUserRepos("google").asSnapshot()

        assertEquals(100, repos.size)
        coVerify(exactly = 1) { remoteDataSource.getUserRepos("google", 1, 100) }
    }
}
