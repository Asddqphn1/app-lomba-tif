package com.example.lombatif.api

import com.example.lombatif.response.LoginReq
import com.example.lombatif.response.ResponseDaftarLomba
import com.example.lombatif.response.ResponseLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("daftarlomba")
    suspend fun getDaftarLomba() : ResponseDaftarLomba
    @POST("/login")
    suspend fun login(@Body request: LoginReq): Response<ResponseLogin>

}