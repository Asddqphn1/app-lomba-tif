package com.example.lombatif.models.get

import com.google.gson.annotations.SerializedName

data class DaftarUsersAdmin(

    @SerializedName("nama") val nama : String?,
    @SerializedName("email") val email : String?,
    @SerializedName("role") val role : String?
)
