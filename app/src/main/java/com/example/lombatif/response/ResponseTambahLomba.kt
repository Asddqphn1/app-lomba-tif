package com.example.lombatif.response

import com.example.lombatif.models.request.RequestLomba

data class ResponseTambahLomba(
    val status : String?,
    val message : String?,
    val data : RequestLomba,
)
