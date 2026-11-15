package com.lucasoliveira.bliss.data.remote.network

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class GithubApiNetworkAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {

        if (Call::class.java != getRawType(returnType)) {
            return null
        }

        if (returnType !is ParameterizedType) {
            throw IllegalStateException("Call return type must be parameterized as Call<Foo> or Call<out Foo>")
        }

        val responseType = getParameterUpperBound(0, returnType)

        if (getRawType(responseType) != GithubApiResponse::class.java) {
            return null
        }

        if (responseType !is ParameterizedType) {
            throw IllegalStateException("Response must be parameterized as GithubApiResponse<Foo> or GithubApiResponse<out Foo>")
        }

        val successBodyType = getParameterUpperBound(0, responseType)

        return GithubApiNetworkAdapter<Any>(successBodyType)
    }
}