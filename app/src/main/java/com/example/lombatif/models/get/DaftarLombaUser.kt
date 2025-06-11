package com.example.lombatif.models.get

import com.google.gson.annotations.SerializedName

// Kelas utama untuk seluruh respons
data class DaftarLombaUser(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<PesertaLombaData>
)

// Kelas untuk setiap objek di dalam array "data"
data class PesertaLombaData(
    @SerializedName("id_peserta_lomba")
    val idPesertaLomba: String,

    @SerializedName("lomba")
    val lomba: LombaDetail,

    @SerializedName("peserta")
    val peserta: PesertaDetail
)

// Kelas untuk objek "lomba"
data class LombaDetail(
    @SerializedName("id")
    val id: String,

    @SerializedName("nama")
    val nama: String,

    @SerializedName("tanggal")
    val tanggal: String,

    @SerializedName("lokasi")
    val lokasi: String,

    @SerializedName("bataswaktu")
    val batasWaktu: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("jenis_lomba")
    val jenisLomba: String
)

// Kelas untuk objek "peserta"
data class PesertaDetail(
    @SerializedName("id")
    val id: String,

    @SerializedName("nama")
    val nama: String,

    @SerializedName("users")
    val users: UserDetail
)

// Kelas untuk objek "users" yang ada di dalam "peserta"
data class UserDetail(
    @SerializedName("email")
    val email: String,

    @SerializedName("nama")
    val nama: String
)
