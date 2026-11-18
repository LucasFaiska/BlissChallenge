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

class GetAvatarUseCaseTest {

    private val avatarRepository = mockk<AvatarRepository>()
    private val getAvatarUseCase = GetAvatarUseCase(avatarRepository)

    @Test
    fun `given repository success, when invoked, then emit avatar and complete`() = runTest {
        val avatar = Avatar(login = "blissapps", id = 223156L, avatarUrl = "https://avatars/223156")
        coEvery { avatarRepository.getAvatar("blissapps") } returns Result.success(avatar)

        getAvatarUseCase("blissapps").test {
            assertEquals(avatar, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `given repository failure, when invoked, then throw DomainException with data message`() = runTest {
        val dataError = IllegalStateException("User not found")
        coEvery { avatarRepository.getAvatar("unknown-user") } returns Result.failure(dataError)

        getAvatarUseCase("unknown-user").test {
            val error = awaitError()
            assertTrue(error is DomainException)
            assertEquals("User not found", error.message)
            assertSame(dataError, error.cause)
        }
    }
}
