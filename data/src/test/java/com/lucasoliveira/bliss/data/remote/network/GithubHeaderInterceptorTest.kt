package com.lucasoliveira.bliss.data.remote.network

import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader.ACCEPT_HEADER
import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader.ACCEPT_VALUE
import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader.USER_AGENT_HEADER
import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader.USER_AGENT_VALUE
import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.Assert.assertEquals
import org.junit.Test

class GithubHeaderInterceptorTest {

    private val interceptor = GithubHeaderInterceptor()

    @Test
    fun `given a request when intercepted then add github mandatory headers`() {
        val chain = mockk<Interceptor.Chain>()
        val originalRequest = Request.Builder()
            .url("https://api.github.com/")
            .build()

        every { chain.request() } returns originalRequest
        every { chain.proceed(any()) } answers {
            val interceptedRequest = firstArg<Request>()

            assertEquals(USER_AGENT_VALUE, interceptedRequest.header(USER_AGENT_HEADER))
            assertEquals(ACCEPT_VALUE, interceptedRequest.header(ACCEPT_HEADER))

            mockk(relaxed = true)
        }

        interceptor.intercept(chain)
    }
}