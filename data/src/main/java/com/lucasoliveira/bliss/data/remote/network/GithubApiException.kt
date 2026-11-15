package com.lucasoliveira.bliss.data.remote.network

sealed class GithubApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

class RateLimitException(
    val resetAtUnixSeconds: Long?
) : GithubApiException("GitHub API rate limit exceeded. Reset at: $resetAtUnixSeconds")

class ValidationException(
    message: String = "Validation failed (422). Check query parameters."
) : GithubApiException(message)

class NoConnectivityException(
    cause: Throwable
) : GithubApiException("No internet connection or timeout", cause)

class HttpCallException(
    val code: Int,
    message: String
) : GithubApiException("HTTP Error $code: $message")

class UnknownApiException(
    message: String
) : GithubApiException(message)