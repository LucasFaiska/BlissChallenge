package com.lucasoliveira.bliss.data.repository

import com.lucasoliveira.bliss.data.local.LocalDataSource
import com.lucasoliveira.bliss.data.local.entity.EmojiEntity
import com.lucasoliveira.bliss.data.remote.RemoteDataSource
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import com.lucasoliveira.bliss.data.remote.network.NoConnectivityException
import com.lucasoliveira.bliss.data.remote.network.RateLimitException
import com.lucasoliveira.bliss.domain.model.Emoji
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class EmojiRepositoryImplTest {

    private val localDataSource = mockk<LocalDataSource>(relaxUnitFun = true)
    private val remoteDataSource = mockk<RemoteDataSource>()
    private val emojiRepository = EmojiRepositoryImpl(localDataSource, remoteDataSource)

    private val thumbsUpEntity = EmojiEntity(name = "+1", url = "https://emoji/thumbs-up.png")
    private val thumbsUp = Emoji(name = "+1", url = "https://emoji/thumbs-up.png")

    @Test
    fun `given cached emojis, when getEmojis is called, then return cached emojis without calling api`() = runTest {
        coEvery { localDataSource.countEmojis() } returns 1
        coEvery { localDataSource.getEmojis() } returns listOf(thumbsUpEntity)

        val result = emojiRepository.getEmojis()

        assertEquals(listOf(thumbsUp), result.getOrNull())
        coVerify(exactly = 0) { remoteDataSource.getEmojis() }
        coVerify(exactly = 0) { localDataSource.insertEmojis(any()) }
    }

    @Test
    fun `given empty cache and api success, when getEmojis is called, then save emojis and return them from cache`() = runTest {
        coEvery { localDataSource.countEmojis() } returns 0
        coEvery { remoteDataSource.getEmojis() } returns GithubApiResponse.Success(
            mapOf("+1" to "https://emoji/thumbs-up.png")
        )
        coEvery { localDataSource.getEmojis() } returns listOf(thumbsUpEntity)

        val result = emojiRepository.getEmojis()

        assertEquals(listOf(thumbsUp), result.getOrNull())
        coVerify(exactly = 1) { localDataSource.insertEmojis(listOf(thumbsUpEntity)) }
    }

    @Test
    fun `given empty cache and rate limit, when getEmojis is called, then return failure with rate limit message`() = runTest {
        val apiError = RateLimitException(resetAtUnixSeconds = null)
        coEvery { localDataSource.countEmojis() } returns 0
        coEvery { remoteDataSource.getEmojis() } returns GithubApiResponse.Error(apiError)

        val result = emojiRepository.getEmojis()

        val error = result.exceptionOrNull()
        assertEquals("API rate limit reached. Try again later", error?.message)
        assertSame(apiError, error?.cause)
        coVerify(exactly = 0) { localDataSource.insertEmojis(any()) }
    }

    @Test
    fun `given empty cache and no connectivity, when getEmojis is called, then return failure with no internet message`() = runTest {
        coEvery { localDataSource.countEmojis() } returns 0
        coEvery { remoteDataSource.getEmojis() } returns GithubApiResponse.Error(NoConnectivityException(IOException()))

        val result = emojiRepository.getEmojis()

        assertTrue(result.isFailure)
        assertEquals("No internet connection", result.exceptionOrNull()?.message)
    }
}
