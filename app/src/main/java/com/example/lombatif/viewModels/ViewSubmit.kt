package com.example.lombatif.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.get.SubmissionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// State untuk UI submission (Pastikan Anda punya sealed interface ini)
sealed interface SubmissionUiState {
    object Loading : SubmissionUiState
    data class Success(val submissions: List<SubmissionItem>) : SubmissionUiState
    data class Error(val message: String) : SubmissionUiState
}

class ViewSubmit : ViewModel() { // Nama kelas sudah disesuaikan
    private val _uiState = MutableStateFlow<SubmissionUiState>(SubmissionUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun fetchSubmissions(userId: String) {
        viewModelScope.launch {
            _uiState.value = SubmissionUiState.Loading
            try {
                // Pastikan nama fungsi di ApiService sudah benar
                val response = Retrofins.api.getSubmissions(userId)

                if (response.isSuccessful && response.body() != null) {
                    // Jika berhasil, kirim data ke state Success
                    _uiState.value = SubmissionUiState.Success(response.body()!!.data)
                } else {
                    // Jika gagal (misal: error 404, 500), kirim pesan error
                    val errorBody = response.errorBody()?.string() ?: "Gagal memuat submission"
                    _uiState.value = SubmissionUiState.Error("Error ${response.code()}: $errorBody")
                }
            } catch (e: Exception) {
                // Jika terjadi exception (misal: tidak ada internet, salah format JSON)
                _uiState.value = SubmissionUiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}