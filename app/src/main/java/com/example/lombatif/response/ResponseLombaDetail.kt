package com.example.lombatif.response


import com.google.gson.annotations.SerializedName

// Model untuk menampung detail sebuah lomba dari endpoint GET .../daftarlomba/{idlomba}
data class ResponseLombaDetail(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: LombaDetailData
)

data class LombaDetailData(
    @SerializedName("id") val id: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("jenis_lomba") val jenisLomba: String,
    @SerializedName("jumlah_tim") val jumlahAnggota: Int // Berapa anggota per tim
)

// Model untuk mengirim data pendaftaran ke endpoint POST .../daftarpeserta/{...}
data class PendaftaranRequest(
    @SerializedName("nama") val namaPeserta: String, // Bisa nama individu atau nama tim
    @SerializedName("nama_anggota") val anggota: List<Anggota>? = null // Opsional, hanya untuk tim
)

data class Anggota(
    @SerializedName("nama_anggota") val namaAnggota: String
)

// Model untuk respons umum (bisa digunakan untuk banyak endpoint)
data class StatusResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)
