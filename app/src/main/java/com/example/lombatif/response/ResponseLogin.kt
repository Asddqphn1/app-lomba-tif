package com.example.lombatif.response


data class ResponseLogin(
    val status : String?,
    val message : String?,
    val token : String?,
    val data : DataUser
)
data class DataUser(
    val id : String?,
    val nama : String?,
    val email : String?,

)
