package com.lucasoliveira.bliss.data.remote.network

internal sealed class GithubApiResponse<out T : Any> {
    data class Success<T : Any>(val body: T) : GithubApiResponse<T>()
    data class Error(val error: GithubApiException) : GithubApiResponse<Nothing>()
}