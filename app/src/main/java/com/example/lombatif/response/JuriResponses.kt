// file: models/response/JuriResponses.kt

package com.example.lombatif.response

import com.google.gson.annotations.SerializedName

// 1. Response untuk endpoint: GET /juri/{idUser}
// Versi BARU yang benar
data class ResponseJuriProfile(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<JuriData> // Mengharapkan Array/List
)

// Objek 'data' di dalam ResponseJuriProfile
data class JuriData(
    @SerializedName("id") val id: String // Ini adalah idJuri yang kita butuhkan
)


// 2. Response untuk endpoint: GET /penilaian/{idJuri}
data class ResponseSubmission(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<SubmissionData>
)

// Objek untuk setiap item di dalam list 'data' pada ResponseSubmission
// Catatan: Jika data class di bawah ini sudah ada di file lain, Anda tidak perlu menuliskannya lagi.
// Cukup pastikan file ini bisa meng-import-nya.
data class SubmissionData(
    @SerializedName("id") val id: String,
    @SerializedName("submission_time") val submissionTime: String,
    @SerializedName("pesertalomba") val pesertaLomba: PesertaLomba,
    @SerializedName("penilaian") val penilaian: List<Any>
)

data class PesertaLomba(
    @SerializedName("id") val id: String,
    @SerializedName("peserta") val peserta: Peserta,
    @SerializedName("lomba") val lomba: Lomba
)

data class Peserta(
    @SerializedName("nama") val nama: String
)

data class Lomba(
    @SerializedName("nama") val nama: String,
    @SerializedName("jenis_lomba") val jenisLomba: String
)
