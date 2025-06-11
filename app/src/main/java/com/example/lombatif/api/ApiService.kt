package com.example.lombatif.api



import com.example.lombatif.models.get.DaftarSubmitUser
import com.example.lombatif.models.request.RequestLogin
import com.example.lombatif.models.request.RequestLomba
import com.example.lombatif.models.request.RequestRegister
import com.example.lombatif.models.request.RequestUpdateJuri
import com.example.lombatif.response.PendaftaranRequest
import com.example.lombatif.response.ResponseAnggotaTim
import com.example.lombatif.response.ResponseDaftarLomba
import com.example.lombatif.response.ResponseJuriAdmin
import com.example.lombatif.response.ResponseLogin
import com.example.lombatif.response.ResponseLombaDetail
import com.example.lombatif.response.ResponsePesertaAdmin
import com.example.lombatif.response.ResponseProfile
import com.example.lombatif.response.ResponseReqRegister
import com.example.lombatif.response.ResponseTambahLomba
import com.example.lombatif.response.ResponseUpdateJuri
import com.example.lombatif.response.ResponseUserAdmin
import com.example.lombatif.response.StatusResponse
import com.example.lombatif.response.responseJuri.ResponseJuriProfile
import com.example.lombatif.response.responseJuri.ResponseSubmission
import com.example.lombatif.response.responsePeserta.ResponseLombaDiikuti
import com.example.lombatif.response.responsePeserta.SubmissionCheckResponse
import com.example.lombatif.response.responsePeserta.SubmissionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // === Auth & Profile ===
    @POST("register")
    suspend fun setRegisterUser(@Body requestRegister: RequestRegister) : Response<ResponseReqRegister>

    @POST("login")
    suspend fun  setLoginUser(@Body requestLogin: RequestLogin) : Response<ResponseLogin>

    @GET("auth/me")
    suspend fun getProfile() : ResponseProfile


    // === Lomba (Umum) ===
    @GET("daftarlomba")
    suspend fun getDaftarLomba() : ResponseDaftarLomba

    @GET("daftarlomba/{lombaId}")
    suspend fun getLombaDetail(@Path("lombaId") lombaId: String): ResponseLombaDetail


    // === Pendaftaran Peserta ===
    @POST("daftarpeserta/{userId}/{lombaId}")
    suspend fun postPendaftaran(
        @Path("userId") userId: String,
        @Path("lombaId") lombaId: String,
        @Body request: PendaftaranRequest
    ): StatusResponse


    // === Submission Peserta ===
    @GET("daftarlomba/userlomba/{userId}")
    suspend fun getLombaDiikuti(@Path("userId") userId: String): ResponseLombaDiikuti

    @GET("submit/submission/{idPesertaLomba}")
    suspend fun checkSubmission(@Path("idPesertaLomba") idPesertaLomba: String): SubmissionCheckResponse

    @POST("submit/{idPesertaLomba}")
    suspend fun postSubmission(
        @Path("idPesertaLomba") idPesertaLomba: String,
        @Body request: SubmissionRequest
    ): StatusResponse

    @DELETE("submit/hapus/{submissionId}")
    suspend fun deleteSubmission(@Path("submissionId") submissionId: String): StatusResponse

    // Mungkin untuk dashboard?
    @GET("submit/users/{userId}")
    suspend fun getSubmissions(@Path("userId") userId: String): Response<DaftarSubmitUser>


    // === Penilaian Juri ===
    @GET("juri/{idUser}")
    suspend fun getJuriProfile(@Path("idUser") idUser: String): ResponseJuriProfile

    @GET("penilaian/{idJuri}")
    suspend fun getSubmissionsForJuri(@Path("idJuri") idJuri: String): ResponseSubmission


    // === Fitur Admin ===
    @GET("users")
    suspend fun  getUsers() : ResponseUserAdmin

    @DELETE("users/{id}")
    suspend fun hapusUser(@Path("id") id: String)

    @GET("juri")
    suspend fun  getJuri() : ResponseJuriAdmin

    @PATCH("juri/{id}")
    suspend fun updateUsertoJuri(
        @Path ("id") id : String,
        @Body requestUpdateJuri: RequestUpdateJuri
    ): Response<ResponseUpdateJuri>

    @PATCH("juri/hapus/{id}")
    suspend fun hapusJuri(@Path("id") id: String)

    @POST("daftarlomba")
    suspend fun postLomba(@Body requestLomba: RequestLomba) : Response<ResponseTambahLomba>

    @DELETE("daftarlomba/{id}")
    suspend fun hapusLomba(@Path("id") id: String)

    @GET("daftarpeserta")
    suspend fun fetchPeserta(@Query("jenis") jenis: String? = null): Response<ResponsePesertaAdmin>

    @GET("daftarpeserta/anggotatim/{id}")
    suspend fun fetchAnggotaTim(@Path("id") id: String): Response<ResponseAnggotaTim>
}

