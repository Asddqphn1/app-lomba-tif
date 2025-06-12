package com.example.lombatif.viewModels.JuriModels


import android.util.Log
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

    // State untuk menyimpan juriId yang valid
    private val _juriId = MutableStateFlow<String?>(null)
    val juriId: StateFlow<String?> = _juriId

    init {
        // Langsung panggil alur pengambilan data saat ViewModel dibuat
        fetchJuriSubmissions()
    }

    fun fetchJuriSubmissions() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // LANGKAH 1: Dapatkan idUser (sudah benar)
                val profileResponse = Retrofins.api.getProfile()
                val idUser = profileResponse.profile?.id
                if (idUser.isNullOrBlank()) {
                    throw Exception("ID User tidak ditemukan dari profil.")
                }

                // LANGKAH 2: Dapatkan profil juri
                val juriProfileResponse = Retrofins.api.getJuriProfile(idUser = idUser)
                if (juriProfileResponse.status != "success") {
                    throw Exception("Gagal mendapatkan data juri dari server.")
                }

                val juriData = juriProfileResponse.data?.firstOrNull()
                    ?: throw Exception("Data juri (dalam list) tidak ditemukan untuk user ini.")

                // --- PERBAIKAN: Gunakan .juriId sesuai nama properti di data class JuriData ---
                val idJuri = juriData.id
                if (idJuri.isNullOrBlank()) {
                    throw Exception("User ini tidak memiliki profil Juri.")
                }

                // Simpan idJuri yang valid ke StateFlow agar bisa digunakan di UI
                _juriId.value = idJuri

                // LANGKAH 3: Dapatkan daftar submission (sudah benar)
                val submissionResponse = Retrofins.api.getSubmissionsForJuri(idJuri = idJuri)
                if (submissionResponse.status == "success") {
                    // Pastikan data class ResponseSubmission mengizinkan data untuk null
                    _submissions.value = submissionResponse.data ?: emptyList()
                } else {
                    throw Exception("Gagal mendapatkan daftar submission.")
                }

            } catch (e: Exception) {
                val errorMessage = e.message ?: "Terjadi kesalahan yang tidak diketahui."
                _error.value = errorMessage
                Log.e("ViewSubmission", "Error fetching submissions: $errorMessage")
            } finally {
                _isLoading.value = false
            }
        }
    }
}