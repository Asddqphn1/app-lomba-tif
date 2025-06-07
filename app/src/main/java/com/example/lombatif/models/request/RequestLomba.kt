package com.example.lombatif.models.request

enum class JenisLomba {
    INDIVIDU,
    TIM
}

data class RequestLomba(
    val id: String,
    val nama: String,
    val tanggal: String,
    val lokasi: String,
    val url: String,
    val bataswaktu: String,
    val deskripsi: String,
    val jenis_lomba: String,
    val jumlah_anggota: Int?
)
