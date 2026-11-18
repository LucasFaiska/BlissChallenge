package com.lucasoliveira.bliss.data.mapper

import com.lucasoliveira.bliss.data.remote.network.HttpCallException
import com.lucasoliveira.bliss.data.remote.network.NoConnectivityException
import com.lucasoliveira.bliss.data.remote.network.RateLimitException
import com.lucasoliveira.bliss.data.remote.network.UnknownApiException
import com.lucasoliveira.bliss.data.remote.network.ValidationException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.Locale
import java.util.TimeZone

class ErrorMapperTest {

    private lateinit var defaultTimeZone: TimeZone
    private lateinit var defaultLocale: Locale

    @Before
    fun setup() {
        defaultTimeZone = TimeZone.getDefault()
        defaultLocale = Locale.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        Locale.setDefault(Locale.US)
    }

    @After
    fun teardown() {
        TimeZone.setDefault(defaultTimeZone)
        Locale.setDefault(defaultLocale)
    }

    @Test
    fun `given rate limit with reset, when toRepositoryException is called, then message has formatted reset time`() {
        val error = RateLimitException(resetAtUnixSeconds = 1672531200L)

        val result = error.toRepositoryException(formatTime = { "14:32" })

        assertEquals("API rate limit reached. Try again at 14:32", result.message)
        assertSame(error, result.cause)
    }

    @Test
    fun `given rate limit with reset and default formatter, when toRepositoryException is called, then message has hour and minute`() {
        val error = RateLimitException(resetAtUnixSeconds = 1672582320L)

        val result = error.toRepositoryException()

        assertEquals("API rate limit reached. Try again at 14:12", result.message)
    }

    @Test
    fun `given rate limit without reset, when toRepositoryException is called, then message asks to try later`() {
        val error = RateLimitException(resetAtUnixSeconds = null)

        val result = error.toRepositoryException()

        assertEquals("API rate limit reached. Try again later", result.message)
        assertSame(error, result.cause)
    }

    @Test
    fun `given no connectivity, when toRepositoryException is called, then message is no internet connection`() {
        val error = NoConnectivityException(IOException())

        val result = error.toRepositoryException()

        assertEquals("No internet connection", result.message)
        assertSame(error, result.cause)
    }

    @Test
    fun `given 404 http error, when toRepositoryException is called, then message is default not found`() {
        val error = HttpCallException(code = 404, message = "Not Found")

        val result = error.toRepositoryException()

        assertEquals("Not found", result.message)
        assertSame(error, result.cause)
    }

    @Test
    fun `given 404 http error and custom not found message, when toRepositoryException is called, then message is the custom one`() {
        val error = HttpCallException(code = 404, message = "Not Found")

        val result = error.toRepositoryException(notFoundMessage = "User blissapps not found")

        assertEquals("User blissapps not found", result.message)
    }

    @Test
    fun `given non 404 http error, when toRepositoryException is called, then message is generic`() {
        val error = HttpCallException(code = 500, message = "Internal Server Error")

        val result = error.toRepositoryException()

        assertEquals("Something went wrong", result.message)
        assertSame(error, result.cause)
    }

    @Test
    fun `given validation error, when toRepositoryException is called, then message is generic`() {
        val error = ValidationException()

        val result = error.toRepositoryException()

        assertEquals("Something went wrong", result.message)
        assertSame(error, result.cause)
    }

    @Test
    fun `given unknown api error, when toRepositoryException is called, then message is generic`() {
        val error = UnknownApiException("Empty response body")

        val result = error.toRepositoryException()

        assertEquals("Something went wrong", result.message)
        assertSame(error, result.cause)
    }
}
