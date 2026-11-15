package com.lucasoliveira.bliss.data.remote.network

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import okhttp3.Headers
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class GithubApiNetworkCallTest {

    private val delegate = mockk<Call<String>>()
    private val githubCall = GithubApiNetworkCall(delegate)
    private val callback = mockk<Callback<GithubApiResponse<String>>>(relaxed = true)

    @Test
    fun `given 200 response when enqueue then return Success`() {
        val slot = slot<Callback<String>>()
        every { delegate.enqueue(capture(slot)) } answers {
            slot.captured.onResponse(delegate, Response.success("data"))
        }

        githubCall.enqueue(callback)

        val responseSlot = slot<Response<GithubApiResponse<String>>>()
        verify { callback.onResponse(any(), capture(responseSlot)) }
        assertTrue(responseSlot.captured.body() is GithubApiResponse.Success)
    }

    @Test
    fun `given 403 response with exhausted rate limit when enqueue then return RateLimitException`() {
        val slot = slot<Callback<String>>()
        every { delegate.enqueue(capture(slot)) } answers {
            slot.captured.onResponse(
                delegate,
                errorResponse(403, "x-ratelimit-remaining" to "0", "x-ratelimit-reset" to "1700000000")
            )
        }

        githubCall.enqueue(callback)

        val responseSlot = slot<Response<GithubApiResponse<String>>>()
        verify { callback.onResponse(any(), capture(responseSlot)) }

        val body = responseSlot.captured.body() as GithubApiResponse.Error
        val error = body.error as RateLimitException
        assertEquals(1700000000L, error.resetAtUnixSeconds)
    }

    @Test
    fun `given 403 response with retry after when enqueue then return RateLimitException`() {
        val slot = slot<Callback<String>>()
        every { delegate.enqueue(capture(slot)) } answers {
            slot.captured.onResponse(delegate, errorResponse(403, "retry-after" to "60"))
        }

        githubCall.enqueue(callback)

        val responseSlot = slot<Response<GithubApiResponse<String>>>()
        verify { callback.onResponse(any(), capture(responseSlot)) }

        val body = responseSlot.captured.body() as GithubApiResponse.Error
        val error = body.error as RateLimitException
        assertNotNull(error.resetAtUnixSeconds)
    }

    @Test
    fun `given 403 response without rate limit headers when enqueue then return HttpCallException`() {
        val slot = slot<Callback<String>>()
        every { delegate.enqueue(capture(slot)) } answers {
            slot.captured.onResponse(delegate, errorResponse(403, "x-ratelimit-remaining" to "42"))
        }

        githubCall.enqueue(callback)

        val responseSlot = slot<Response<GithubApiResponse<String>>>()
        verify { callback.onResponse(any(), capture(responseSlot)) }

        val body = responseSlot.captured.body() as GithubApiResponse.Error
        val error = body.error as HttpCallException
        assertEquals(403, error.code)
    }

    @Test
    fun `given 429 response when enqueue then return RateLimitException`() {
        val slot = slot<Callback<String>>()
        every { delegate.enqueue(capture(slot)) } answers {
            slot.captured.onResponse(delegate, Response.error(429, "".toResponseBody(null)))
        }

        githubCall.enqueue(callback)

        val responseSlot = slot<Response<GithubApiResponse<String>>>()
        verify { callback.onResponse(any(), capture(responseSlot)) }

        val body = responseSlot.captured.body() as GithubApiResponse.Error
        assertTrue(body.error is RateLimitException)
    }

    @Test
    fun `given 422 response when enqueue then return ValidationException`() {
        val slot = slot<Callback<String>>()
        every { delegate.enqueue(capture(slot)) } answers {
            slot.captured.onResponse(delegate, Response.error(422, "".toResponseBody(null)))
        }

        githubCall.enqueue(callback)

        val responseSlot = slot<Response<GithubApiResponse<String>>>()
        verify { callback.onResponse(any(), capture(responseSlot)) }

        val body = responseSlot.captured.body() as GithubApiResponse.Error
        assertTrue(body.error is ValidationException)
    }

    @Test
    fun `given 500 response when enqueue then return HttpCallException`() {
        val slot = slot<Callback<String>>()
        every { delegate.enqueue(capture(slot)) } answers {
            slot.captured.onResponse(delegate, Response.error(500, "".toResponseBody(null)))
        }

        githubCall.enqueue(callback)

        val responseSlot = slot<Response<GithubApiResponse<String>>>()
        verify { callback.onResponse(any(), capture(responseSlot)) }

        val body = responseSlot.captured.body() as GithubApiResponse.Error
        assertTrue(body.error is HttpCallException)
    }

    @Test
    fun `given IOException when enqueue then return NoConnectivityException`() {
        val slot = slot<Callback<String>>()
        every { delegate.enqueue(capture(slot)) } answers {
            slot.captured.onFailure(delegate, IOException())
        }

        githubCall.enqueue(callback)

        val responseSlot = slot<Response<GithubApiResponse<String>>>()
        verify { callback.onResponse(any(), capture(responseSlot)) }

        val body = responseSlot.captured.body() as GithubApiResponse.Error
        assertTrue(body.error is NoConnectivityException)
    }

    @Test
    fun `given unknown error when enqueue then return HttpCallException`() {
        val slot = slot<Callback<String>>()
        every { delegate.enqueue(capture(slot)) } answers {
            slot.captured.onResponse(delegate, Response.error(404, "".toResponseBody(null)))
        }

        githubCall.enqueue(callback)

        val responseSlot = slot<Response<GithubApiResponse<String>>>()
        verify { callback.onResponse(any(), capture(responseSlot)) }

        val body = responseSlot.captured.body() as GithubApiResponse.Error
        assertTrue(body.error is HttpCallException)
    }

    @Test
    fun `given unknown throwable, when onFailure is called, then return UnknownApiException`() {
        val slot = slot<Callback<String>>()
        val unknownException = RuntimeException("Something went wrong")
        every { delegate.enqueue(capture(slot)) } answers {
            slot.captured.onFailure(delegate, unknownException)
        }

        githubCall.enqueue(callback)

        val responseSlot = slot<Response<GithubApiResponse<String>>>()
        verify { callback.onResponse(any(), capture(responseSlot)) }

        val body = responseSlot.captured.body() as GithubApiResponse.Error
        assertTrue(body.error is UnknownApiException)
    }

    private fun errorResponse(code: Int, vararg headers: Pair<String, String>): Response<String> {
        val rawResponse = okhttp3.Response.Builder()
            .code(code)
            .message("Error")
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url("https://api.github.com/").build())
            .headers(Headers.headersOf(*headers.flatMap { listOf(it.first, it.second) }.toTypedArray()))
            .build()
        return Response.error("".toResponseBody(null), rawResponse)
    }
}
