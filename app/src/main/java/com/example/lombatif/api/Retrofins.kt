package com.example.lombatif.api

import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager

object Retrofins {
    private val cookies = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Content-Type", "application/json")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        })
        .cookieJar(JavaNetCookieJar(CookieManager()))
        .build()
    val api : ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://hono-api-lomba-tif-production.up.railway.app/")
            .client(cookies)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService:: class.java)
    }

}