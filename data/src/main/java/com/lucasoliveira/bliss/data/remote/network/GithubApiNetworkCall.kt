package com.lucasoliveira.bliss.data.remote.network

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

internal class GithubApiNetworkCall<S : Any>(
    private val delegate: Call<S>
) : Call<GithubApiResponse<S>> {

    override fun clone(): Call<GithubApiResponse<S>> =
        GithubApiNetworkCall(delegate.clone())

    override fun execute(): Response<GithubApiResponse<S>> {
        throw UnsupportedOperationException("GithubApiNetworkCall doesn't support execute")
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()

    override fun enqueue(callback: Callback<GithubApiResponse<S>>) {
        delegate.enqueue(GithubApiCallback(this, callback))
    }

    private class GithubApiCallback<S : Any>(
        private val networkCall: GithubApiNetworkCall<S>,
        private val originalCallback: Callback<GithubApiResponse<S>>
    ) : Callback<S> {

        override fun onResponse(call: Call<S>, response: Response<S>) {
            val githubApiResponse = response.toGithubApiResponse()

            originalCallback.onResponse(
                networkCall,
                Response.success(githubApiResponse)
            )
        }

        override fun onFailure(call: Call<S>, throwable: Throwable) {
            val exception = when (throwable) {
                is IOException -> NoConnectivityException(throwable)
                else -> UnknownApiException(throwable.message ?: "Unknown Error")
            }

            originalCallback.onResponse(
                networkCall,
                Response.success(GithubApiResponse.Error(exception))
            )
        }
    }
}