package com.example.lombatif.api

import com.example.lombatif.models.request.RequestRegister
import com.example.lombatif.response.ResponseDaftarLomba
import com.example.lombatif.response.ResponseReqRegister
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ApiService {
    @GET("daftarlomba")
    suspend fun getDaftarLomba() : ResponseDaftarLomba

    @POST("register")
    suspend fun setRegisterUser(@Body requestRegister: RequestRegister) : Response<ResponseReqRegister>


}