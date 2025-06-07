package com.example.lombatif.models.request

data class RequestDaftarLombaPeserta(
    val nama: String,
    val nama_anggota: List<String>? = null
)
