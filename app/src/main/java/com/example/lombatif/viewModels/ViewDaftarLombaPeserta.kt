package com.example.lombatif.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.ApiService
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.request.RequestDaftarLombaPeserta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DaftarLombaUiState {
    object Idle : DaftarLombaUiState()
    object Loading : DaftarLombaUiState()
    data class Success(val message: String) : DaftarLombaUiState()
    data class Error(val message: String) : DaftarLombaUiState()
}

class ViewDaftarLombaPeserta : ViewModel() {

    private val _uiState = MutableStateFlow<DaftarLombaUiState>(DaftarLombaUiState.Idle)
    val uiState: StateFlow<DaftarLombaUiState> = _uiState

    fun daftarLomba(idUser: String, idLomba: String, namaTim: String, namaAnggota: List<String> = emptyList()) {
        viewModelScope.launch {
            _uiState.value = DaftarLombaUiState.Loading
            try {
                val body = RequestDaftarLombaPeserta(
                    nama = namaTim,
                    nama_anggota = namaAnggota
                )
                val response = Retrofins.api.daftarLomba(idUser, idLomba, body)
                Log.d("ViewDaftarLombaPeserta", "Response code: ${response}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _uiState.value = DaftarLombaUiState.Success(
                        message = responseBody?.message ?: "Berhasil"
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Terjadi kesalahan"
                    _uiState.value = DaftarLombaUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = DaftarLombaUiState.Error(e.localizedMessage ?: "Terjadi kesalahan")
            }
        }
    }
}
