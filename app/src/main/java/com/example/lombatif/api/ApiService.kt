package com.example.lombatif.api

import com.example.lombatif.response.ResponseDaftarLomba
import retrofit2.http.GET

interface ApiService {
    @GET("daftarlomba")
    suspend fun getDaftarLomba() : ResponseDaftarLomba
}