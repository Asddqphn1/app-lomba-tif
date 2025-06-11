package com.example.lombatif.models.get

data class AnggotaTimData(
    val nama: String,
    val id: String,
    val pesertalomba: List<PesertaLombaDataa>,
    val anggotaTim: List<AnggotaData>
)

data class PesertaLombaDataa(
    val lomba: LombaNama
)

data class LombaNama(
    val nama: String
)

data class AnggotaData(
    val nama: String
)
