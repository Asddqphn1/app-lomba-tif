package com.example.lombatif.models.get.modelsPeserta

import com.google.gson.annotations.SerializedName

// Kelas untuk seluruh respons dari API klasemen
data class KlasemenModels(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<KlasemenItemRaw>
)

// Kelas untuk satu item mentah dari API
data class KlasemenItemRaw(
    @SerializedName("nilai") val nilai: String?,
    @SerializedName("peserta") val peserta: KlasemenPeserta,
    @SerializedName("lomba") val lomba: KlasemenLomba
)

data class KlasemenPeserta(
    @SerializedName("nama") val nama: String,
    @SerializedName("email") val email: String
)

data class KlasemenLomba(
    @SerializedName("nama") val nama: String
)

// Kelas yang sudah diproses untuk ditampilkan di UI
data class KlasemenEntry(
    val rank: Int?,
    val namaPeserta: String,
    val rataRataNilai: Double?,
    val jumlahPenilaian: Int?
)
