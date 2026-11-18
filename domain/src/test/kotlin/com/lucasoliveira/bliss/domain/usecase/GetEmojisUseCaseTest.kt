package com.lucasoliveira.bliss.domain.usecase

import app.cash.turbine.test
import com.lucasoliveira.bliss.domain.error.DomainException
import com.lucasoliveira.bliss.domain.model.Emoji
import com.lucasoliveira.bliss.domain.repository.EmojiRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetEmojisUseCaseTest {

    private val emojiRepository = mockk<EmojiRepository>()
    private val getEmojisUseCase = GetEmojisUseCase(emojiRepository)

    @Test
    fun `given repository success, when invoked, then emit emojis and complete`() = runTest {
        val emojis = listOf(Emoji(name = "+1", url = "https://emoji/thumbs-up.png"))
        coEvery { emojiRepository.getEmojis() } returns Result.success(emojis)

        getEmojisUseCase().test {
            assertEquals(emojis, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `given repository failure, when invoked, then throw DomainException with data message`() = runTest {
        val dataError = IllegalStateException("API rate limit exceeded. Try again at 14:32")
        coEvery { emojiRepository.getEmojis() } returns Result.failure(dataError)

        getEmojisUseCase().test {
            val error = awaitError()
            assertTrue(error is DomainException)
            assertEquals("API rate limit exceeded. Try again at 14:32", error.message)
            assertSame(dataError, error.cause)
        }
    }
}
