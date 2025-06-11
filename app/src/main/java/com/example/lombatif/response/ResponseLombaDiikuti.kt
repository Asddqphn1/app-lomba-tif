package com.example.lombatif.response

import com.example.lombatif.models.get.PesertaLombaInfo


import com.google.gson.annotations.SerializedName

data class ResponseLombaDiikuti(
    val data: List<DataLombaUser>
)

data class DataLombaUser(
    @SerializedName("id_peserta_lomba") val idPesertaLomba: String,
    val lomba: Lomba,
    val peserta: Peserta
)

data class Lomba(
    val id: String,
    val nama: String,
    val bataswaktu: String,
    val url: String,
    val lokasi: String,
    @SerializedName("jenis_lomba") val jenisLomba: String
)

data class Peserta(
    val nama: String
)

// Model untuk mengecek submission yang sudah ada
data class SubmissionCheckResponse(
    val data: SubmissionData?
)

data class SubmissionData(
    val id: String,
    @SerializedName("file_url") val fileUrl: String,
    @SerializedName("submission_time") val submissionTime: String,
    @SerializedName("pesertalomba") val pesertaLomba: PesertaLombaInfo
)

// Model untuk request body saat submit URL
data class SubmissionRequest(
    val url: String
)

// Model untuk respons upload ke Cloudinary
data class CloudinaryResponse(
    @SerializedName("secure_url") val secureUrl: String
)
