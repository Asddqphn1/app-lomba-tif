package com.example.lombatif.models.get

import com.google.gson.annotations.SerializedName
import java.util.Date

data class DaftarLomba(
    @SerializedName("id") val id : String?,
    @SerializedName("url") val url : String?,
    @SerializedName("nama") val nama : String?,
    @SerializedName("tanggal") val tanggal : String?,
    @SerializedName("lokasi") val lokasi : String?,
    @SerializedName("deskripsi") val deskripsi : String?,
    @SerializedName("bataswaktu") val batasWaktu : String?,
    @SerializedName("jenis_lomba") val jenisLomba : String?,
    @SerializedName("jumlah_tim") val jumlah_tim :Int?
)
