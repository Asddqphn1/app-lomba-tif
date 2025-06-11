package com.example.lombatif.models.get


import com.google.gson.annotations.SerializedName

// Kelas utama untuk seluruh respons
data class DaftarSubmitUser(
    @SerializedName("status")
    val status: String,

    @SerializedName("data")
    val data: List<SubmissionItem>
)

// Kelas untuk satu item submission
data class SubmissionItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("file_url")
    val fileUrl: String,

    @SerializedName("submission_time")
    val submissionTime: String,

    @SerializedName("pesertalomba")
    val pesertaLomba: PesertaLombaInfo
)

// Kelas untuk objek pesertalomba
data class PesertaLombaInfo(
    @SerializedName("lomba")
    val lomba: LombaInfo
)

// Kelas untuk objek lomba yang ada di dalam pesertalomba
data class LombaInfo(
    @SerializedName("id")
    val id: String,

    @SerializedName("nama")
    val nama: String,

    @SerializedName("jenis_lomba")
    val jenisLomba: String
)
