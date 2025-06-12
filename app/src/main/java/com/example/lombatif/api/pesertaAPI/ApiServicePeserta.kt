package com.example.lombatif.api.pesertaAPI

import com.example.lombatif.models.get.modelsPeserta.KlasemenModels
import com.example.lombatif.models.get.modelsPeserta.PenilaianModels
import com.example.lombatif.models.get.modelsPeserta.SertifikatModels
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiServicePeserta {

    @GET("penilaian/peserta/{userId}/penilaian")
    suspend fun getPenilaian(
        @Path("userId") userId: String
    ): PenilaianModels

    // ENDPOINT BARU UNTUK KLASMEN
    @GET("penilaian/daftarnilai/{lombaId}")
    suspend fun getKlasemen(@Path("lombaId") lombaId: String): KlasemenModels

    @GET("sertifikat/{lombaId}")
    suspend fun getSertifikat(@Path("lombaId") lombaId: String): SertifikatModels
}