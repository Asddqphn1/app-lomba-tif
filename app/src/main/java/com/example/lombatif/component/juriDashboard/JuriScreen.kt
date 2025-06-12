package com.example.lombatif.component.juriDashboard

// Sealed class untuk mendefinisikan rute navigasi dengan aman.
sealed class JuriScreens(val route: String) {
    // Rute untuk layar daftar submission
    object SubmissionList : JuriScreens("submission_list_screen")


    object PenilaianDetail : JuriScreens("penilaian_detail_screen/{submissionId}") {
        // Fungsi bantuan untuk membuat rute lengkap dengan ID-nya
        fun createRoute(submissionId: String) = "penilaian_detail_screen/$submissionId"
    }
}