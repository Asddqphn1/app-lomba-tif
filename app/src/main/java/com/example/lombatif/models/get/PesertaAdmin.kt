package com.example.lombatif.models.get

data class Lomba(
    val nama: String,
    val jenis_lomba: String
)

data class PesertaLomba(
    val lomba_id: String,
    val lomba: Lomba
)

data class PesertaAdmin(
    val id: String,
    val users_id: String,
    val nama: String,
    val created_at: String,
    val pesertalomba: List<PesertaLomba>
)




