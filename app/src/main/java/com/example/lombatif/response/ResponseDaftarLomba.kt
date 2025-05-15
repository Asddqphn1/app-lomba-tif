package com.example.lombatif.response

import com.example.lombatif.models.get.DaftarLomba
import com.google.gson.annotations.SerializedName

data class ResponseDaftarLomba(
    @SerializedName("data") val daftarLomba : List<DaftarLomba>
)
