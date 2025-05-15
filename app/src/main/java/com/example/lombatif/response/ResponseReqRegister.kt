package com.example.lombatif.response

import com.example.lombatif.models.request.RequestRegister

data class ResponseReqRegister(
    val status : String?,
    val message : String?,
    val data : RequestRegister,
)

