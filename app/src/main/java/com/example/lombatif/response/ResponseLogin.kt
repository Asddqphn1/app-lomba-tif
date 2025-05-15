package com.example.lombatif.response

data class ResponseLogin(
    val status: String,
    val message: String,
    val data: UserData,
)
