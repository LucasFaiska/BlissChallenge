package com.lucasoliveira.bliss.domain.usecase

import app.cash.turbine.test
import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.model.Avatar
import com.lucasoliveira.bliss.domain.repository.AvatarRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAvatarsUseCaseTest {

    private val avatarRepository = mockk<AvatarRepository>()
    private val getAvatarsUseCase = GetAvatarsUseCase(avatarRepository)

    @Test
    fun `given repository success, when invoked, then emit avatars and complete`() = runTest {
        val avatars = listOf(Avatar(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156"))
        coEvery { avatarRepository.getAvatars() } returns Result.success(avatars)

        getAvatarsUseCase().test {
            assertEquals(avatars, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `given repository failure, when invoked, then throw DomainException with data message`() = runTest {
        val dataError = IllegalStateException("Could not load avatars")
        coEvery { avatarRepository.getAvatars() } returns Result.failure(dataError)

        getAvatarsUseCase().test {
            val error = awaitError()
            assertTrue(error is DomainException)
            assertEquals("Could not load avatars", error.message)
            assertSame(dataError, error.cause)
        }
    }
}
