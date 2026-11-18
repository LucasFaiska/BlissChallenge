package com.lucasoliveira.bliss.domain.usecase

import app.cash.turbine.test
import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.repository.AvatarRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteAvatarUseCaseTest {

    private val avatarRepository = mockk<AvatarRepository>()
    private val deleteAvatarUseCase = DeleteAvatarUseCase(avatarRepository)

    @Test
    fun `given repository success, when invoked, then emit unit and complete`() = runTest {
        coEvery { avatarRepository.deleteAvatar("blissapps") } returns Result.success(Unit)

        deleteAvatarUseCase("blissapps").test {
            assertEquals(Unit, awaitItem())
            awaitComplete()
        }
        coVerify(exactly = 1) { avatarRepository.deleteAvatar("blissapps") }
    }

    @Test
    fun `given repository failure, when invoked, then throw DomainException with data message`() = runTest {
        val dataError = IllegalStateException("Could not delete avatar")
        coEvery { avatarRepository.deleteAvatar("blissapps") } returns Result.failure(dataError)

        deleteAvatarUseCase("blissapps").test {
            val error = awaitError()
            assertTrue(error is DomainException)
            assertEquals("Could not delete avatar", error.message)
            assertSame(dataError, error.cause)
        }
    }
}
