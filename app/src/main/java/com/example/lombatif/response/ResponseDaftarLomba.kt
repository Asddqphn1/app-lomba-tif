package com.example.lombatif.response

import com.example.lombatif.models.DaftarLomba
import com.google.gson.annotations.SerializedName

data class ResponseDaftarLomba(
    @SerializedName("data") val daftarLomba : List<DaftarLomba>
)
