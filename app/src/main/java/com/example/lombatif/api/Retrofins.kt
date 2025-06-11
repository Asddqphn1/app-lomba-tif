package com.example.lombatif.api


import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy

object Retrofins {
    private val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    // Store token dynamically (can be set after login)
    private var authToken: String? = null

    fun setAuthToken(token: String?) {
        authToken = token
    }

    private val client = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(cookieManager))
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            // Add Authorization header if token is available
            authToken?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }
            val response = chain.proceed(requestBuilder.build())
            response
        }
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://hono-api-lomba-tif-production.up.railway.app/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}