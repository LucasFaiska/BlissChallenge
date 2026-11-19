package com.lucasoliveira.bliss.data.remote

import com.lucasoliveira.bliss.data.remote.api.GithubApiService
import com.lucasoliveira.bliss.data.remote.dto.RepoResponseDto
import com.lucasoliveira.bliss.data.remote.dto.UserResponseDto
import com.lucasoliveira.bliss.data.remote.network.GithubApiResponse
import javax.inject.Inject

internal interface RemoteDataSource {

    suspend fun getEmojis(): GithubApiResponse<Map<String, String>>

    suspend fun getUser(username: String): GithubApiResponse<UserResponseDto>

    suspend fun getUserRepos(username: String, page: Int, perPage: Int): GithubApiResponse<List<RepoResponseDto>>
}

internal class RemoteDataSourceImpl @Inject constructor(
    private val apiService: GithubApiService
) : RemoteDataSource {

    override suspend fun getEmojis(): GithubApiResponse<Map<String, String>> =
        apiService.getEmojis()

    override suspend fun getUser(username: String): GithubApiResponse<UserResponseDto> =
        apiService.getUser(username)

    override suspend fun getUserRepos(
        username: String,
        page: Int,
        perPage: Int
    ): GithubApiResponse<List<RepoResponseDto>> =
        apiService.getUserRepos(username, page, perPage)
}
