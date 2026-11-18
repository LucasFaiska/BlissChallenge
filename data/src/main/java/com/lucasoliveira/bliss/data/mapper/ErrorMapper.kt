package com.lucasoliveira.bliss.data.mapper

import com.lucasoliveira.bliss.data.remote.network.GithubApiException
import com.lucasoliveira.bliss.data.remote.network.HttpCallException
import com.lucasoliveira.bliss.data.remote.network.HttpStatus
import com.lucasoliveira.bliss.data.remote.network.NoConnectivityException
import com.lucasoliveira.bliss.data.remote.network.RateLimitException
import com.lucasoliveira.bliss.data.remote.network.UnknownApiException
import com.lucasoliveira.bliss.data.remote.network.ValidationException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal const val RATE_LIMIT_MESSAGE = "API rate limit reached. Try again at %s"
internal const val RATE_LIMIT_WITHOUT_RESET_MESSAGE = "API rate limit reached. Try again later"
internal const val NO_CONNECTIVITY_MESSAGE = "No internet connection"
internal const val NOT_FOUND_MESSAGE = "Not found"
internal const val GENERIC_ERROR_MESSAGE = "Something went wrong"

private const val TIME_PATTERN = "HH:mm"
private const val MILLIS_IN_SECOND = 1000L

internal fun GithubApiException.toRepositoryException(
    notFoundMessage: String = NOT_FOUND_MESSAGE,
    formatTime: (Long) -> String = ::formatTimeOfDay,
): Exception = when (this) {
    is RateLimitException -> Exception(rateLimitMessage(resetAtUnixSeconds, formatTime), this)
    is NoConnectivityException -> Exception(NO_CONNECTIVITY_MESSAGE, this)
    is HttpCallException -> Exception(if (code == HttpStatus.NOT_FOUND) notFoundMessage else GENERIC_ERROR_MESSAGE, this)
    is ValidationException -> Exception(GENERIC_ERROR_MESSAGE, this)
    is UnknownApiException -> Exception(GENERIC_ERROR_MESSAGE, this)
}

private fun rateLimitMessage(resetAtUnixSeconds: Long?, formatTime: (Long) -> String): String =
    resetAtUnixSeconds?.let { RATE_LIMIT_MESSAGE.format(formatTime(it)) } ?: RATE_LIMIT_WITHOUT_RESET_MESSAGE

private fun formatTimeOfDay(unixSeconds: Long): String =
    SimpleDateFormat(TIME_PATTERN, Locale.getDefault()).format(Date(unixSeconds * MILLIS_IN_SECOND))
