package com.example.lombatif.models.get

import com.google.gson.annotations.SerializedName

data class JuriAdmin(
    @SerializedName("id") val id : String?,
    @SerializedName("nama") val nama : String?,
    @SerializedName("users") val users : User
)
data class User (
    @SerializedName("email") val email: String?
)
