package com.example.lombatif.models.get

import com.google.gson.annotations.SerializedName
import java.util.Date

data class DaftarLomba(
    @SerializedName("url") val url : String?,
    @SerializedName("nama") val nama : String?,
    @SerializedName("tanggal") val tanggal : Date?,
    @SerializedName("lokasi") val lokasi : String?,
    @SerializedName("deskripsi") val deskripsi : String?,
    @SerializedName("bataswaktu") val batasWaktu : String?,
    @SerializedName("jenis_lomba") val jenisLomba : String?
)
