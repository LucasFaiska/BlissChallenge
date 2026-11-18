package com.lucasoliveira.bliss.data.remote.api

import com.lucasoliveira.bliss.data.remote.dto.UserResponseDto
import com.lucasoliveira.bliss.data.remote.network.GithubApiNetworkAdapterFactory
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import com.lucasoliveira.bliss.data.remote.network.GithubHeaderInterceptor
import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader
import com.lucasoliveira.bliss.data.remote.network.HttpCallException
import com.lucasoliveira.bliss.data.remote.network.NoConnectivityException
import com.lucasoliveira.bliss.data.remote.network.RateLimitException
import com.lucasoliveira.bliss.data.remote.network.UnknownApiException
import com.lucasoliveira.bliss.data.remote.network.ValidationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class GithubApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: GithubApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        val json = Json { ignoreUnknownKeys = true }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(GithubHeaderInterceptor())
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(GithubApiNetworkAdapterFactory())
            .build()
            .create(GithubApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `given valid response, when getEmojis is called, then return Success with emojis map`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(readJsonFile("emojis_success.json")))

        val response = apiService.getEmojis()

        val body = (response as GithubApiResponse.Success).body
        assertEquals(3, body.size)
        assertEquals("https://github.githubassets.com/images/icons/emoji/unicode/1f44d.png?v8", body["+1"])
        assertEquals("https://github.githubassets.com/images/icons/emoji/octocat.png?v8", body["octocat"])
    }

    @Test
    fun `when getEmojis is called, then send GET to emojis path with github headers`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(readJsonFile("emojis_success.json")))

        apiService.getEmojis()

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/emojis", request.path)
        assertEquals(GithubHttpHeader.USER_AGENT_VALUE, request.getHeader(GithubHttpHeader.USER_AGENT_HEADER))
        assertEquals(GithubHttpHeader.ACCEPT_VALUE, request.getHeader(GithubHttpHeader.ACCEPT_HEADER))
    }

    @Test
    fun `given real github user payload, when getUser is called, then return Success with user`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(readJsonFile("user_success.json")))

        val response = apiService.getUser("blissapps")

        val expected = UserResponseDto(
            login = "blissapps",
            id = 223156L,
            avatarUrl = "https://avatars.githubusercontent.com/u/223156?v=4"
        )
        assertEquals(expected, (response as GithubApiResponse.Success).body)
    }

    @Test
    fun `when getUser is called, then send GET to users path with github headers`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(readJsonFile("user_success.json")))

        apiService.getUser("blissapps")

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/users/blissapps", request.path)
        assertEquals(GithubHttpHeader.USER_AGENT_VALUE, request.getHeader(GithubHttpHeader.USER_AGENT_HEADER))
        assertEquals(GithubHttpHeader.ACCEPT_VALUE, request.getHeader(GithubHttpHeader.ACCEPT_HEADER))
    }

    @Test
    fun `given 403 with exhausted rate limit, when getEmojis is called, then return RateLimitException`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(403)
                .addHeader(GithubHttpHeader.RATE_LIMIT_REMAINING_HEADER, "0")
                .addHeader(GithubHttpHeader.RATE_LIMIT_RESET_HEADER, "1672531200")
                .setBody("""{"message": "API rate limit exceeded"}""")
        )

        val response = apiService.getEmojis()

        val error = (response as GithubApiResponse.Error).error
        assertEquals(1672531200L, (error as RateLimitException).resetAtUnixSeconds)
    }

    @Test
    fun `given 429 with retry after, when getUser is called, then return RateLimitException with computed reset`() = runBlocking {
        val retryAfterSeconds = 30L
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(429)
                .addHeader(GithubHttpHeader.RATE_LIMIT_RETRY_AFTER_HEADER, retryAfterSeconds.toString())
        )
        val requestedAt = System.currentTimeMillis() / 1000

        val response = apiService.getUser("blissapps")

        val error = (response as GithubApiResponse.Error).error
        val resetAt = requireNotNull((error as RateLimitException).resetAtUnixSeconds)
        assertTrue(resetAt >= requestedAt + retryAfterSeconds)
    }

    @Test
    fun `given 422, when getUser is called, then return ValidationException`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(422))

        val response = apiService.getUser("blissapps")

        assertTrue((response as GithubApiResponse.Error).error is ValidationException)
    }

    @Test
    fun `given unknown user, when getUser is called, then return HttpCallException with 404`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("""{"message": "Not Found"}"""))

        val response = apiService.getUser("unknown-user")

        val error = (response as GithubApiResponse.Error).error
        assertEquals(404, (error as HttpCallException).code)
    }

    @Test
    fun `given 500, when getEmojis is called, then return HttpCallException with 500`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val response = apiService.getEmojis()

        val error = (response as GithubApiResponse.Error).error
        assertEquals(500, (error as HttpCallException).code)
    }

    @Test
    fun `given 204 without body, when getEmojis is called, then return UnknownApiException`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(204))

        val response = apiService.getEmojis()

        assertTrue((response as GithubApiResponse.Error).error is UnknownApiException)
    }

    @Test
    fun `given connection drop, when getEmojis is called, then return NoConnectivityException`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        val response = apiService.getEmojis()

        assertTrue((response as GithubApiResponse.Error).error is NoConnectivityException)
    }

    private fun readJsonFile(fileName: String): String =
        requireNotNull(javaClass.classLoader?.getResource(fileName)).readText()
}
