package com.lucasoliveira.bliss.data.remote.network

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

internal class GithubApiNetworkAdapter<S : Any>(
    private val successType: Type
) : CallAdapter<S, Call<GithubApiResponse<S>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<S>): Call<GithubApiResponse<S>> {
        return GithubApiNetworkCall(call)
    }
}