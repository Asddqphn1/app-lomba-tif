package com.example.lombatif.models.get.modelsPeserta


import com.google.gson.annotations.SerializedName

// Kelas untuk seluruh respons dari API sertifikat
data class SertifikatModels(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: SertifikatData? // Bisa null jika tidak ada sertifikat
)

// Kelas untuk data sertifikat itu sendiri
data class SertifikatData(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("lomba_id") val lombaId: String
)

