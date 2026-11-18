package com.lucasoliveira.bliss.data.remote.api

import com.lucasoliveira.bliss.data.remote.dto.UserResponseDto
import com.lucasoliveira.bliss.data.remote.network.GithubApiNetworkAdapterFactory
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader
import com.lucasoliveira.bliss.data.remote.network.HttpCallException
import com.lucasoliveira.bliss.data.remote.network.NoConnectivityException
import com.lucasoliveira.bliss.data.remote.network.RateLimitException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
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

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
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

        assertEquals("/emojis", mockWebServer.takeRequest().path)
        val body = (response as GithubApiResponse.Success).body
        assertEquals(3, body.size)
        assertEquals("https://github.githubassets.com/images/icons/emoji/unicode/1f44d.png?v8", body["+1"])
        assertEquals("https://github.githubassets.com/images/icons/emoji/octocat.png?v8", body["octocat"])
    }

    @Test
    fun `given rate limit exceeded, when getEmojis is called, then return RateLimitException`() = runBlocking {
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
    fun `given connection drop, when getEmojis is called, then return NoConnectivityException`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START))

        val response = apiService.getEmojis()

        assertTrue((response as GithubApiResponse.Error).error is NoConnectivityException)
    }

    @Test
    fun `given valid response, when getUser is called, then return Success with user`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(readJsonFile("user_success.json")))

        val response = apiService.getUser("blissapps")

        assertEquals("/users/blissapps", mockWebServer.takeRequest().path)
        val expected = UserResponseDto(
            login = "blissapps",
            id = 223156L,
            avatarUrl = "https://avatars.githubusercontent.com/u/223156?v=4"
        )
        assertEquals(expected, (response as GithubApiResponse.Success).body)
    }

    @Test
    fun `given unknown user, when getUser is called, then return HttpCallException with 404`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("""{"message": "Not Found"}"""))

        val response = apiService.getUser("unknown-user")

        val error = (response as GithubApiResponse.Error).error
        assertEquals(404, (error as HttpCallException).code)
    }

    private fun readJsonFile(fileName: String): String =
        requireNotNull(javaClass.classLoader?.getResource(fileName)).readText()
}
