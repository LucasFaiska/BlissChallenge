package com.lucasoliveira.bliss.data.remote

import com.lucasoliveira.bliss.data.remote.api.GithubApiService
import com.lucasoliveira.bliss.data.remote.dto.UserResponseDto
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import com.lucasoliveira.bliss.data.remote.network.HttpCallException
import com.lucasoliveira.bliss.data.remote.network.RateLimitException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteDataSourceImplTest {

    private val apiService = mockk<GithubApiService>()
    private val remoteDataSource = RemoteDataSourceImpl(apiService)

    @Test
    fun `given api success, when getEmojis is called, then return Success from api`() = runTest {
        val expected = GithubApiResponse.Success(mapOf("+1" to "https://emoji/thumbs-up.png"))
        coEvery { apiService.getEmojis() } returns expected

        assertEquals(expected, remoteDataSource.getEmojis())
        coVerify(exactly = 1) { apiService.getEmojis() }
    }

    @Test
    fun `given api error, when getEmojis is called, then return Error from api`() = runTest {
        val expected = GithubApiResponse.Error(RateLimitException(resetAtUnixSeconds = 1672531200L))
        coEvery { apiService.getEmojis() } returns expected

        assertEquals(expected, remoteDataSource.getEmojis())
    }

    @Test
    fun `given api success, when getUser is called, then return Success from api`() = runTest {
        val user = UserResponseDto(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")
        val expected = GithubApiResponse.Success(user)
        coEvery { apiService.getUser("blissapps") } returns expected

        assertEquals(expected, remoteDataSource.getUser("blissapps"))
        coVerify(exactly = 1) { apiService.getUser("blissapps") }
    }

    @Test
    fun `given api error, when getUser is called, then return Error from api`() = runTest {
        val expected = GithubApiResponse.Error(HttpCallException(404, "Not Found"))
        coEvery { apiService.getUser("unknown-user") } returns expected

        assertEquals(expected, remoteDataSource.getUser("unknown-user"))
    }
}
