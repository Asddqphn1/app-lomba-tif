package com.example.lombatif.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofins {
    val api : ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://192.168.1.7:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService:: class.java)
    }
}