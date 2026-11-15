package com.lucasoliveira.bliss.data.remote.network

internal object GithubHttpHeader {
    const val RATE_LIMIT_REMAINING_HEADER = "x-ratelimit-remaining"
    const val RATE_LIMIT_RESET_HEADER = "x-ratelimit-reset"
    const val RATE_LIMIT_RETRY_AFTER_HEADER = "retry-after"
    const val USER_AGENT_HEADER = "User-Agent"
    const val ACCEPT_HEADER = "Accept"
    const val USER_AGENT_VALUE = "LucasOliveiraBlissChallengeApp/1.0"
    const val ACCEPT_VALUE = "application/vnd.github.v3+json"
}