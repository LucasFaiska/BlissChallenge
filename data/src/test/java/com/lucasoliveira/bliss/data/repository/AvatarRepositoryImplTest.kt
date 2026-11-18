package com.lucasoliveira.bliss.data.repository

import com.lucasoliveira.bliss.data.local.LocalDataSource
import com.lucasoliveira.bliss.data.local.entity.AvatarEntity
import com.lucasoliveira.bliss.data.remote.RemoteDataSource
import com.lucasoliveira.bliss.data.remote.dto.UserResponseDto
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import com.lucasoliveira.bliss.data.remote.network.HttpCallException
import com.lucasoliveira.bliss.data.remote.network.NoConnectivityException
import com.lucasoliveira.bliss.data.remote.network.RateLimitException
import com.lucasoliveira.bliss.domain.model.Avatar
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class AvatarRepositoryImplTest {

    private val localDataSource = mockk<LocalDataSource>(relaxUnitFun = true)
    private val remoteDataSource = mockk<RemoteDataSource>()
    private val avatarRepository = AvatarRepositoryImpl(localDataSource, remoteDataSource)

    private val blissEntity = AvatarEntity(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")
    private val blissAvatar = Avatar(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")

    @Test
    fun `given cached avatar, when getAvatar is called, then return cached avatar without calling api`() = runTest {
        coEvery { localDataSource.getAvatarByLogin("blissapps") } returns blissEntity

        val result = avatarRepository.getAvatar("blissapps")

        assertEquals(blissAvatar, result.getOrNull())
        coVerify(exactly = 0) { remoteDataSource.getUser(any()) }
        coVerify(exactly = 0) { localDataSource.insertAvatar(any()) }
    }

    @Test
    fun `given avatar not cached and api success, when getAvatar is called, then save and return avatar`() = runTest {
        coEvery { localDataSource.getAvatarByLogin("blissapps") } returns null
        coEvery { remoteDataSource.getUser("blissapps") } returns GithubApiResponse.Success(
            UserResponseDto(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")
        )

        val result = avatarRepository.getAvatar("blissapps")

        assertEquals(blissAvatar, result.getOrNull())
        coVerify(exactly = 1) { localDataSource.insertAvatar(blissEntity) }
    }

    @Test
    fun `given avatar not cached and user not found, when getAvatar is called, then return failure with user not found message`() = runTest {
        val apiError = HttpCallException(code = 404, message = "Not Found")
        coEvery { localDataSource.getAvatarByLogin("unknown-user") } returns null
        coEvery { remoteDataSource.getUser("unknown-user") } returns GithubApiResponse.Error(apiError)

        val result = avatarRepository.getAvatar("unknown-user")

        val error = result.exceptionOrNull()
        assertEquals("User unknown-user not found", error?.message)
        assertSame(apiError, error?.cause)
        coVerify(exactly = 0) { localDataSource.insertAvatar(any()) }
    }

    @Test
    fun `given avatar not cached and rate limit, when getAvatar is called, then return failure with rate limit message`() = runTest {
        coEvery { localDataSource.getAvatarByLogin("blissapps") } returns null
        coEvery { remoteDataSource.getUser("blissapps") } returns GithubApiResponse.Error(
            RateLimitException(resetAtUnixSeconds = null)
        )

        val result = avatarRepository.getAvatar("blissapps")

        assertEquals("API rate limit reached. Try again later", result.exceptionOrNull()?.message)
    }

    @Test
    fun `given avatar not cached and no connectivity, when getAvatar is called, then return failure with no internet message`() = runTest {
        coEvery { localDataSource.getAvatarByLogin("blissapps") } returns null
        coEvery { remoteDataSource.getUser("blissapps") } returns GithubApiResponse.Error(
            NoConnectivityException(IOException())
        )

        val result = avatarRepository.getAvatar("blissapps")

        assertTrue(result.isFailure)
        assertEquals("No internet connection", result.exceptionOrNull()?.message)
    }

    @Test
    fun `given cached avatars, when getAvatars is called, then return them as domain avatars`() = runTest {
        coEvery { localDataSource.getAvatars() } returns listOf(blissEntity)

        val result = avatarRepository.getAvatars()

        assertEquals(listOf(blissAvatar), result.getOrNull())
    }

    @Test
    fun `given no cached avatars, when getAvatars is called, then return empty list`() = runTest {
        coEvery { localDataSource.getAvatars() } returns emptyList()

        val result = avatarRepository.getAvatars()

        assertEquals(emptyList<Avatar>(), result.getOrNull())
    }

    @Test
    fun `given login, when deleteAvatar is called, then delete it locally and return success`() = runTest {
        val result = avatarRepository.deleteAvatar("blissapps")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { localDataSource.deleteAvatarByLogin("blissapps") }
    }
}
