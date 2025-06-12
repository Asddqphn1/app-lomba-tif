package com.example.lombatif.models.get.modelsPeserta


import com.google.gson.annotations.SerializedName

// Kelas wrapper utama untuk seluruh respons
data class PenilaianModels(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<PesertaPenilaian>
)

// Merepresentasikan data satu peserta (biasanya hanya user saat ini)
data class PesertaPenilaian(
    @SerializedName("id") val id: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("pesertalomba") val pesertaLomba: List<PesertaLombaDetail>
)

// Merepresentasikan satu lomba yang diikuti oleh peserta
data class PesertaLombaDetail(
    @SerializedName("lomba") val lomba: LombaInfoPenilaian,
    // Submission bisa null jika peserta belum submit apa-apa
    @SerializedName("submission") val submission: SubmissionInfo?
)

// Info dasar tentang lomba
data class LombaInfoPenilaian(
    @SerializedName("id") val id: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("jenis_lomba") val jenisLomba: String,
    @SerializedName("tanggal") val tanggal: String
)

// Info tentang submission, yang berisi list penilaian
data class SubmissionInfo(
    @SerializedName("id") val id: String,
    @SerializedName("penilaian") val penilaian: List<PenilaianDetail>
)

// Detail dari satu penilaian oleh satu juri
data class PenilaianDetail(
    @SerializedName("nilai_penilaian") val nilaiPenilaian: String,
    @SerializedName("deskripsi_penilaian") val deskripsiPenilaian: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("juri") val juri: JuriInfo
)

// Info tentang juri yang memberi nilai
data class JuriInfo(
    @SerializedName("id") val id: String,
    @SerializedName("users") val users: UserInfo
)

// Info dasar pengguna (untuk juri)
data class UserInfo(
    @SerializedName("id") val id: String,
    @SerializedName("nama") val nama: String
)

