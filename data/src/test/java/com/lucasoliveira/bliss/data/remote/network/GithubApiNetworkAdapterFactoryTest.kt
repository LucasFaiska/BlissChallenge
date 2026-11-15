package com.lucasoliveira.bliss.data.remote.network

import io.mockk.mockk
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class GithubApiNetworkAdapterFactoryTest {
    private val factory = GithubApiNetworkAdapterFactory()
    private val retrofit = mockk<Retrofit>()

    @Test
    fun `given valid parameterized GithubApiResponse when get then return adapter`() {
        val innerType = object : ParameterizedType {
            override fun getActualTypeArguments(): Array<Type> = arrayOf(String::class.java)
            override fun getRawType(): Type = GithubApiResponse::class.java
            override fun getOwnerType(): Type? = null
        }

        val returnType = object : ParameterizedType {
            override fun getActualTypeArguments(): Array<Type> = arrayOf(innerType)
            override fun getRawType(): Type = Call::class.java
            override fun getOwnerType(): Type? = null
        }

        val adapter = factory.get(returnType, emptyArray(), retrofit)
        assertNotNull(adapter)
    }

    @Test
    fun `given non call return type when get then return null`() {
        val returnType = String::class.java
        val adapter = factory.get(returnType, emptyArray(), retrofit)
        assertNull(adapter)
    }

    @Test(expected = IllegalStateException::class)
    fun `given non parameterized call when get then throw exception`() {
        val returnType = Call::class.java
        factory.get(returnType, emptyArray(), retrofit)
    }
}