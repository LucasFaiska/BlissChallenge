package com.lucasoliveira.bliss.data.remote.api

import com.lucasoliveira.bliss.data.remote.dto.UserResponseDto
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface GithubApiService {

    @GET("emojis")
    suspend fun getEmojis(): GithubApiResponse<Map<String, String>>

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): GithubApiResponse<UserResponseDto>
}
