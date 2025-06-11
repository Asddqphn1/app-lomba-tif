package com.example.lombatif.viewModels.JuriModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins

import com.example.lombatif.response.responseJuri.SubmissionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Menggunakan ViewModel biasa karena tidak perlu Context lagi
class ViewSubmission : ViewModel() {

    // State untuk UI
    private val _submissions = MutableStateFlow<List<SubmissionData>>(emptyList())
    val submissions: StateFlow<List<SubmissionData>> = _submissions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        // Langsung panggil alur pengambilan data saat ViewModel dibuat
        fetchJuriSubmissions()
    }

    fun fetchJuriSubmissions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // --- LANGKAH 1: Dapatkan profil user untuk mendapat idUser ---
                val profileResponse = Retrofins.api.getProfile()
                val idUser = profileResponse.profile.id

                // Pemeriksaan awal untuk memastikan idUser ada
                if (idUser.isNullOrBlank()) {
                    throw Exception("ID User tidak ditemukan dari profil. Sesi mungkin tidak valid.")
                }

                // --- LANGKAH 2: Dapatkan profil juri untuk mendapat idJuri ---
                val juriProfileResponse = Retrofins.api.getJuriProfile(idUser = idUser)

                // Periksa status response juri
                if (juriProfileResponse.status != "success") {
                    throw Exception("Gagal mendapatkan data juri dari server.")
                }
                val juriData = juriProfileResponse.data.firstOrNull()
                    ?: throw Exception("Data juri tidak ditemukan untuk user ini.")

                val idJuri = juriData.id

                // Pemeriksaan untuk memastikan idJuri ada
                if (idJuri.isNullOrBlank()) {
                    throw Exception("User ini tidak memiliki profil Juri.")
                }

                // --- LANGKAH 3: Dapatkan daftar submission untuk juri tersebut ---
                val submissionResponse = Retrofins.api.getSubmissionsForJuri(
                    idJuri = idJuri
                )
                if (submissionResponse.status == "success") {
                    _submissions.value = submissionResponse.data
                } else {
                    throw Exception("Gagal mendapatkan daftar submission dari server.")
                }

            } catch (e: Exception) {
                // Jika terjadi error di langkah manapun, tangkap dan tampilkan pesannya
                _error.value = e.message ?: "Terjadi kesalahan yang tidak diketahui."
                e.printStackTrace()
            } finally {
                // Apapun yang terjadi, hentikan loading indicator
                _isLoading.value = false
            }
        }
    }
}