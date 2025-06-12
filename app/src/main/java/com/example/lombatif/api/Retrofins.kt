package com.example.lombatif.api


import com.example.lombatif.api.pesertaAPI.ApiServicePeserta
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import okhttp3.logging.HttpLoggingInterceptor

object Retrofins {
    private val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // BODY akan menampilkan semua detail
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
        .addInterceptor(loggingInterceptor) // <-- TAMBAHKAN INTERCEPTOR DI SINI
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://hono-api-lomba-tif-production.up.railway.app/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    val apiServicePeserta : ApiServicePeserta by lazy {
        Retrofit.Builder()
            .baseUrl("https://hono-api-lomba-tif-production.up.railway.app/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServicePeserta::class.java)
    }

    val cloudinaryApi: CloudinaryApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/")
            .client(client) // Bisa gunakan client yang sama
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApi::class.java)
    }
}
