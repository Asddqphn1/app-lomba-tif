package com.example.lombatif.response

import com.example.lombatif.models.get.DaftarUsersAdmin
import com.google.gson.annotations.SerializedName

data class ResponseUserAdmin(
    @SerializedName("data") val users : List<DaftarUsersAdmin>
)
