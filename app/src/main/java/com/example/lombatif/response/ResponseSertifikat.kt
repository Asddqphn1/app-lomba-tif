package com.example.lombatif.response

data class ResponseKirimSertifikat(
    val status: String?,
    val message: String?,
    val data: SertifikatData?
)

data class SertifikatData(
    val id: String?,
    val lomba_id: String?,
    val url: String?
)
