package com.example.gogolookhomework.network

import com.example.gogolookhomework.misc.API_KEI
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val url = request.url.newBuilder().addQueryParameter("key", API_KEI).build()
        val newRequest: Request = request.newBuilder()
            .url(url)
            .build()
        return chain.proceed(newRequest)
    }
}