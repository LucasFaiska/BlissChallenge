package com.lucasoliveira.bliss.data.remote.network

import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader.RATE_LIMIT_REMAINING_HEADER
import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader.RATE_LIMIT_RESET_HEADER
import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader.RATE_LIMIT_RETRY_AFTER_HEADER
import retrofit2.Response

private const val RATE_LIMIT_EXHAUSTED = "0"

internal fun <S : Any> Response<S>.toGithubApiResponse(): GithubApiResponse<S> {
    if (this.isSuccessful) {
        this.body()?.let {
            return GithubApiResponse.Success(it)
        }
        return GithubApiResponse.Error(UnknownApiException("Empty response body"))
    }

    return when (val code = this.code()) {
        HttpStatus.TOO_MANY_REQUESTS -> {
            GithubApiResponse.Error(toRateLimitException())
        }

        HttpStatus.FORBIDDEN -> {
            if (isRateLimited()) {
                GithubApiResponse.Error(toRateLimitException())
            } else {
                GithubApiResponse.Error(HttpCallException(code, this.message()))
            }
        }

        HttpStatus.UNPROCESSABLE_ENTITY -> {
            GithubApiResponse.Error(ValidationException())
        }

        else -> {
            GithubApiResponse.Error(HttpCallException(code, this.message()))
        }
    }
}

private fun Response<*>.isRateLimited(): Boolean {
    val remaining = this.headers()[RATE_LIMIT_REMAINING_HEADER]
    val retryAfter = this.headers()[RATE_LIMIT_RETRY_AFTER_HEADER]
    return remaining == RATE_LIMIT_EXHAUSTED || retryAfter != null
}

private fun Response<*>.toRateLimitException(): RateLimitException {
    val resetUnix = this.headers()[RATE_LIMIT_RESET_HEADER]?.toLongOrNull()
    val retryAfterSeconds = this.headers()[RATE_LIMIT_RETRY_AFTER_HEADER]?.toLongOrNull()
    val finalResetUnix = resetUnix ?: retryAfterSeconds?.let { (System.currentTimeMillis() / 1000) + it }
    return RateLimitException(resetAtUnixSeconds = finalResetUnix)
}
