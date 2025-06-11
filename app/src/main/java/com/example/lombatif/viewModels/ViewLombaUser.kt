package com.example.lombatif.viewModels // Sesuaikan dengan package Anda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.get.PesertaLombaData // Import model data yang akan digunakan di UI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sealed interface untuk merepresentasikan semua kemungkinan state pada UI
sealed interface LombaUiState {
    object Loading : LombaUiState // State ketika data sedang dimuat
    data class Success(val lombaList: List<PesertaLombaData>) : LombaUiState // State ketika data berhasil didapat
    data class Error(val message: String) : LombaUiState // State ketika terjadi error
}

class ViewLombaUser : ViewModel() {

    // _lombaState adalah MutableStateFlow privat yang hanya bisa diubah di dalam ViewModel
    private val _lombaState = MutableStateFlow<LombaUiState>(LombaUiState.Loading)

    // lombaState adalah StateFlow publik yang hanya bisa dibaca (read-only) oleh UI
    val lombaState = _lombaState.asStateFlow()

    fun fetchUserLomba(userId: String) {
        // Gunakan viewModelScope untuk menjalankan coroutine yang terikat dengan lifecycle ViewModel
        viewModelScope.launch {
            // Set state ke Loading sebelum memulai panggilan API
            _lombaState.value = LombaUiState.Loading

            try {
                // Memanggil fungsi dari ApiService
                val response = Retrofins.api.getDashboardData(userId)

                // BENAR
                if (response.isSuccessful) {
                    val body = response.body()
                    // Langsung akses "data" dari body
                    val dataLomba = body?.data

                    if (dataLomba != null) {
                        _lombaState.value = LombaUiState.Success(dataLomba)
                    } else {
                        _lombaState.value = LombaUiState.Error("Data tidak ditemukan atau format respons salah.")
                    }
                }else {
                    // Jika respons tidak berhasil (kode 4xx atau 5xx)
                    _lombaState.value = LombaUiState.Error("Gagal memuat data: ${response.message()}")
                }
            } catch (e: Exception) {
                // Menangani error jaringan atau exception lainnya
                _lombaState.value = LombaUiState.Error("Terjadi kesalahan: ${e.message}")
            }
        }
    }
}