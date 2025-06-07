package com.example.lombatif.response

data class ResponseDaftarLombaPeserta(
    val status: String,
    val message: String,
    val data: PesertaData? = null
)
data class PesertaData(
    val id: String,
    val users_id: String,
    val nama: String
)
