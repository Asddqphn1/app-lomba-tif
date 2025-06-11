package com.example.lombatif.models.get

data class AnggotaTimData(
    val nama: String,
    val id: String,
    val pesertalomba: List<PesertaLombaData>,
    val anggotaTim: List<AnggotaData>
)

data class PesertaLombaData(
    val lomba: LombaNama
)

data class LombaNama(
    val nama: String
)

data class AnggotaData(
    val nama: String
)
