// file: data/model/SubmissionModel.kt (atau nama file model Anda)

package com.example.lombatif.data.model

import com.google.gson.annotations.SerializedName

// Membuat ApiResponse menjadi generic agar bisa dipakai ulang
data class ApiResponse<T>(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: T
)

// Model untuk respons dari endpoint /auth/me
data class UserProfile(
    @SerializedName("id") val id: String,
    // tambahkan properti lain jika ada, misal: email, nama, role
)

// Model untuk respons dari endpoint /juri/{idUser}
data class JuriProfile(
    @SerializedName("id") val id: String,
)

// Model untuk data submission (tetap sama)
data class SubmissionData(
    @SerializedName("id") val id: String,
    @SerializedName("submission_time") val submissionTime: String,
    @SerializedName("pesertalomba") val pesertaLomba: PesertaLomba,
    @SerializedName("penilaian") val penilaian: List<Any>
)

// ... (Data class PesertaLomba, Peserta, Lomba tetap sama) ...
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