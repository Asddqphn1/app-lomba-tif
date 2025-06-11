package com.example.lombatif.api

import com.example.lombatif.models.get.DaftarLombaUser
import com.example.lombatif.models.get.DaftarSubmitUser
import com.example.lombatif.models.request.RequestDaftarLombaPeserta
import com.example.lombatif.models.request.RequestLogin
import com.example.lombatif.models.request.RequestLomba
import com.example.lombatif.response.ResponseDaftarLomba
import com.example.lombatif.models.request.RequestRegister
import com.example.lombatif.models.request.RequestUpdateJuri
import com.example.lombatif.response.ResponseAnggotaTim
import com.example.lombatif.response.ResponseDaftarLombaPeserta
import com.example.lombatif.response.ResponseJuriAdmin
import com.example.lombatif.response.ResponseLogin
import com.example.lombatif.response.ResponsePesertaAdmin
import com.example.lombatif.response.PendaftaranRequest
import com.example.lombatif.response.ResponseLombaDetail
import com.example.lombatif.response.ResponseLombaDiikuti
import com.example.lombatif.response.ResponseProfile
import com.example.lombatif.response.ResponseReqRegister
import com.example.lombatif.response.ResponseTambahLomba
import com.example.lombatif.response.ResponseUpdateJuri
import com.example.lombatif.response.ResponseUserAdmin
import com.example.lombatif.response.StatusResponse
import com.example.lombatif.response.SubmissionCheckResponse
import com.example.lombatif.response.SubmissionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("daftarlomba")
    suspend fun getDaftarLomba() : ResponseDaftarLomba

    // Endpoint untuk mendapatkan detail satu lomba
    @GET("daftarlomba/{lombaId}")
    suspend fun getLombaDetail(@Path("lombaId") lombaId: String): ResponseLombaDetail

    // Endpoint untuk mengirim data pendaftaran peserta
    @POST("daftarpeserta/{userId}/{lombaId}")
    suspend fun postPendaftaran(
        @Path("userId") userId: String,
        @Path("lombaId") lombaId: String,
        @Body request: PendaftaranRequest
    ): StatusResponse

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
    
    @GET("daftarpeserta")
    suspend fun fetchPeserta(
        @Query("jenis") jenis: String? = null
    ): Response<ResponsePesertaAdmin>

    @GET("daftarpeserta/anggotatim/{id}")
    suspend fun fetchAnggotaTim(@Path("id") id: String): Response<ResponseAnggotaTim>
    
    @GET("daftarlomba/userlomba/{userId}")
    suspend fun getDashboardData(
        @Path("userId") userId: String
    ): Response<DaftarLombaUser> // <-- Langsung gunakan kelas yang strukturnya benar

    @GET("submit/users/{userId}") // <-- Asumsi endpoint, harap disesuaikan
    suspend fun getSubmissions(
        @Path("userId") userId: String
    ): Response<DaftarSubmitUser>

    // GET Lomba yang diikuti user
    @GET("daftarlomba/userlomba/{userId}")
    suspend fun getLombaDiikuti(@Path("userId") userId: String): ResponseLombaDiikuti

    // GET Cek submission yang ada
    @GET("submit/submission/{idPesertaLomba}")
    suspend fun checkSubmission(@Path("idPesertaLomba") idPesertaLomba: String): SubmissionCheckResponse

    // POST Melakukan submission
    @POST("submit/{idPesertaLomba}")
    suspend fun postSubmission(
        @Path("idPesertaLomba") idPesertaLomba: String,
        @Body request: SubmissionRequest
    ): StatusResponse

    // DELETE Menghapus submission
    @DELETE("submit/hapus/{submissionId}")
    suspend fun deleteSubmission(@Path("submissionId") submissionId: String): StatusResponse

}
