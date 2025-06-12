// Lokasi: com/example/lombatif/response/DetailSubmissionResponse.kt

package com.example.lombatif.response

import com.google.gson.annotations.SerializedName

// --- Data Class untuk GET /submit/{id} ---
data class DetailSubmissionResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("data") val data: DetailSubmissionData?
)

data class DetailSubmissionData(
    @SerializedName("id") val id: String?,
    // Nama properti di JSON adalah "submission_time"
    @SerializedName("submission_time") val submissionTime: String?,
    // Nama properti di JSON adalah "file"
    @SerializedName("file") val fileUrl: String?,
    @SerializedName("pesertalomba") val pesertaLomba: PesertaLombaDetail?
)

data class PesertaLombaDetail(
    @SerializedName("peserta") val peserta: PesertaDetail?,
    @SerializedName("lomba") val lomba: LombaDetail?
)

data class PesertaDetail(
    @SerializedName("nama") val nama: String?
)

data class LombaDetail(
    @SerializedName("nama") val nama: String?,
    // Nama properti di JSON adalah "jenis_lomba"
    @SerializedName("jenis_lomba") val jenisLomba: String?
)


// --- Data Class untuk POST /penilaian/{...} ---
data class PenilaianRequest(
    val nilai: Int,
    val catatan: String
)
