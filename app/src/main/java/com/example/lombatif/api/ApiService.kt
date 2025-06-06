package com.example.lombatif.api

import com.example.lombatif.models.request.RequestDaftarLombaPeserta
import com.example.lombatif.models.request.RequestLogin
import com.example.lombatif.models.request.RequestLomba
import com.example.lombatif.response.ResponseDaftarLomba
import com.example.lombatif.models.request.RequestRegister
import com.example.lombatif.models.request.RequestUpdateJuri
import com.example.lombatif.response.ResponseDaftarLombaPeserta
import com.example.lombatif.response.ResponseJuriAdmin
import com.example.lombatif.response.ResponseLogin
import com.example.lombatif.response.ResponseProfile
import com.example.lombatif.response.ResponseReqRegister
import com.example.lombatif.response.ResponseTambahLomba
import com.example.lombatif.response.ResponseUpdateJuri
import com.example.lombatif.response.ResponseUserAdmin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("daftarlomba")
    suspend fun getDaftarLomba() : ResponseDaftarLomba


    @POST("register")
    suspend fun setRegisterUser(@Body requestRegister: RequestRegister) : Response<ResponseReqRegister>

    @POST("login")
    suspend fun  setLoginUser(@Body requestLogin: RequestLogin) : Response<ResponseLogin>

    @GET("users")
    suspend fun  getUsers() : ResponseUserAdmin

    @GET("juri")
    suspend fun  getJuri() : ResponseJuriAdmin

    @GET("auth/me")
    suspend fun getProfile() : ResponseProfile

    @PATCH("juri/{id}")
    suspend fun updateUsertoJuri(
        @Path ("id") id : String,
        @Body requestUpdateJuri: RequestUpdateJuri
    ): Response<ResponseUpdateJuri>

    @POST("daftarlomba")
    suspend fun PostLomba(
        @Body requestUpdateJuri: RequestLomba
    ) : Response<ResponseTambahLomba>

    @DELETE("daftarlomba/{id}")
    suspend fun hapusLomba(@Path("id") id: String)

    @DELETE("users/{id}")
    suspend fun hapusUser(@Path("id") id: String)

    @PATCH("juri/hapus/{id}")
    suspend fun hapusJuri(@Path("id") id: String)

    @POST("daftarpeserta/{idpeserta}/{idlomba}")
    suspend fun daftarLomba(
        @Path("idpeserta") id: String,
        @Path("idlomba") idLomba: String,
        @Body body: RequestDaftarLombaPeserta
    ): Response<ResponseDaftarLombaPeserta>

}
