package com.lucasoliveira.bliss.data.remote.api

import com.lucasoliveira.bliss.data.remote.dto.RepoResponseDto
import com.lucasoliveira.bliss.data.remote.dto.UserResponseDto
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface GithubApiService {

    @GET("emojis")
    suspend fun getEmojis(): GithubApiResponse<Map<String, String>>

    @GET("users/{username}")
    suspend fun getUser(
        @Path("username") username: String
    ): GithubApiResponse<UserResponseDto>

    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): GithubApiResponse<List<RepoResponseDto>>
}
