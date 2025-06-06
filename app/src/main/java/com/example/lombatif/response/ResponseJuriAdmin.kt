package com.example.lombatif.response

import com.example.lombatif.models.get.JuriAdmin
import com.google.gson.annotations.SerializedName

data class ResponseJuriAdmin(
    @SerializedName("data") val juriAdmin : List<JuriAdmin>

)
