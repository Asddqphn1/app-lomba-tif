package com.example.lombatif.response

import com.example.lombatif.models.get.PesertaAdmin

data class ResponsePesertaAdmin(
    val status: String,
    val message: String,
    val data: List<PesertaAdmin>
)
