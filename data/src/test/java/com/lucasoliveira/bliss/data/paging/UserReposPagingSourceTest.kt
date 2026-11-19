package com.lucasoliveira.bliss.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lucasoliveira.bliss.data.remote.RemoteDataSource
import com.lucasoliveira.bliss.data.remote.dto.RepoResponseDto
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import com.lucasoliveira.bliss.data.remote.network.NoConnectivityException
import com.lucasoliveira.bliss.data.remote.network.RateLimitException
import com.lucasoliveira.bliss.domain.model.Repo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException

class UserReposPagingSourceTest {

    private val remoteDataSource = mockk<RemoteDataSource>()
    private val pagingSource = UserReposPagingSource(remoteDataSource, username = "google", pageSize = PAGE_SIZE)

    private fun repoDto(id: Long) = RepoResponseDto(id = id, fullName = "google/repo-$id", isPrivate = false)
    private fun repo(id: Long) = Repo(id = id, fullName = "google/repo-$id", isPrivate = false)

    private fun refreshParams() = PagingSource.LoadParams.Refresh<Int>(key = null, loadSize = PAGE_SIZE, placeholdersEnabled = false)
    private fun appendParams(page: Int) = PagingSource.LoadParams.Append(key = page, loadSize = PAGE_SIZE, placeholdersEnabled = false)

    @Test
    fun `given full first page, when refresh, then request page 1 and return page with next key 2`() = runTest {
        coEvery { remoteDataSource.getUserRepos("google", 1, PAGE_SIZE) } returns GithubApiResponse.Success(listOf(repoDto(1), repoDto(2)))

        val result = pagingSource.load(refreshParams())

        assertEquals(
            PagingSource.LoadResult.Page(data = listOf(repo(1), repo(2)), prevKey = null, nextKey = 2),
            result
        )
        coVerify(exactly = 1) { remoteDataSource.getUserRepos("google", 1, PAGE_SIZE) }
    }

    @Test
    fun `given full middle page, when append with key 3, then request page 3 and return keys 2 and 4`() = runTest {
        coEvery { remoteDataSource.getUserRepos("google", 3, PAGE_SIZE) } returns GithubApiResponse.Success(listOf(repoDto(5), repoDto(6)))

        val result = pagingSource.load(appendParams(3))

        assertEquals(
            PagingSource.LoadResult.Page(data = listOf(repo(5), repo(6)), prevKey = 2, nextKey = 4),
            result
        )
    }

    @Test
    fun `given partial page, when append, then return page without next key`() = runTest {
        coEvery { remoteDataSource.getUserRepos("google", 4, PAGE_SIZE) } returns GithubApiResponse.Success(listOf(repoDto(7)))

        val result = pagingSource.load(appendParams(4)) as PagingSource.LoadResult.Page

        assertEquals(listOf(repo(7)), result.data)
        assertNull(result.nextKey)
    }

    @Test
    fun `given empty page, when append, then return empty page without next key`() = runTest {
        coEvery { remoteDataSource.getUserRepos("google", 5, PAGE_SIZE) } returns GithubApiResponse.Success(emptyList())

        val result = pagingSource.load(appendParams(5)) as PagingSource.LoadResult.Page

        assertEquals(emptyList<Repo>(), result.data)
        assertNull(result.nextKey)
    }

    @Test
    fun `given rate limit, when refresh, then return error with rate limit message`() = runTest {
        coEvery { remoteDataSource.getUserRepos("google", 1, PAGE_SIZE) } returns GithubApiResponse.Error(
            RateLimitException(resetAtUnixSeconds = null)
        )

        val result = pagingSource.load(refreshParams()) as PagingSource.LoadResult.Error

        assertEquals("API rate limit reached. Try again later", result.throwable.message)
    }

    @Test
    fun `given no connectivity, when append, then return error with no internet message`() = runTest {
        coEvery { remoteDataSource.getUserRepos("google", 2, PAGE_SIZE) } returns GithubApiResponse.Error(
            NoConnectivityException(IOException())
        )

        val result = pagingSource.load(appendParams(2)) as PagingSource.LoadResult.Error

        assertEquals("No internet connection", result.throwable.message)
    }

    @Test
    fun `given anchor in second page, when getRefreshKey, then return that page key`() {
        val pages = listOf(
            PagingSource.LoadResult.Page(data = listOf(repo(1), repo(2)), prevKey = null, nextKey = 2),
            PagingSource.LoadResult.Page(data = listOf(repo(3), repo(4)), prevKey = 1, nextKey = 3),
        )
        val state = PagingState(pages = pages, anchorPosition = 3, config = PagingConfig(pageSize = PAGE_SIZE), leadingPlaceholderCount = 0)

        assertEquals(2, pagingSource.getRefreshKey(state))
    }

    @Test
    fun `given anchor in first page, when getRefreshKey, then return first page key`() {
        val pages = listOf(
            PagingSource.LoadResult.Page(data = listOf(repo(1), repo(2)), prevKey = null, nextKey = 2),
        )
        val state = PagingState(pages = pages, anchorPosition = 0, config = PagingConfig(pageSize = PAGE_SIZE), leadingPlaceholderCount = 0)

        assertEquals(1, pagingSource.getRefreshKey(state))
    }

    @Test
    fun `given no anchor, when getRefreshKey, then return null`() {
        val state = PagingState<Int, Repo>(pages = emptyList(), anchorPosition = null, config = PagingConfig(pageSize = PAGE_SIZE), leadingPlaceholderCount = 0)

        assertNull(pagingSource.getRefreshKey(state))
    }

    private companion object {
        const val PAGE_SIZE = 2
    }
}
