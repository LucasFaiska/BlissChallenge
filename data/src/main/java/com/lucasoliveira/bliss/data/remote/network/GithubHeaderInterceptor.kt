package com.lucasoliveira.bliss.data.remote.network

import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader.ACCEPT_HEADER
import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader.ACCEPT_VALUE
import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader.USER_AGENT_HEADER
import com.lucasoliveira.bliss.data.remote.network.GithubHttpHeader.USER_AGENT_VALUE
import okhttp3.Interceptor
import okhttp3.Response

internal class GithubHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(USER_AGENT_HEADER, USER_AGENT_VALUE)
            .header(ACCEPT_HEADER, ACCEPT_VALUE)
            .build()
        return chain.proceed(request)
    }
}