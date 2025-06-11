package com.example.lombatif.response

import com.example.lombatif.models.get.AnggotaTimData

data class ResponseAnggotaTim(
    val status: String,
    val message: String,
    val data: AnggotaTimData
)
