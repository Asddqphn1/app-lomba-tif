package com.example.lombatif.api


import com.example.lombatif.response.CloudinaryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface CloudinaryApi {
    @Multipart
    @POST("v1_1/dkkoi3qc0/raw/upload") // Ganti "dkkoi3qc0" dengan cloud_name Anda
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody
    ): CloudinaryResponse
}