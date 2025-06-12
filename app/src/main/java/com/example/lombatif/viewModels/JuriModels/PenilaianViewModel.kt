package com.example.lombatif.viewModels.JuriModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.response.DetailSubmissionData
import com.example.lombatif.response.PenilaianRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PenilaianViewModel : ViewModel() {

    private val apiService = Retrofins.api

    private val _submissionDetail = MutableStateFlow<DetailSubmissionData?>(null)
    val submissionDetail: StateFlow<DetailSubmissionData?> = _submissionDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    fun getDetailSubmission(submissionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = apiService.getDetailSubmission(submissionId)

                // --- PERBAIKAN: TAMBAHKAN LOG INI UNTUK MELIHAT DATA MENTAH ---
                Log.d("PenilaianVM", "Raw Response for submission $submissionId: ${response.body()}")

                if (response.isSuccessful) {
                    _submissionDetail.value = response.body()?.data
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Gagal memuat detail: ${response.code()} ${response.message()}"
                    Log.e("PenilaianViewModel", "Error Body: $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Terjadi kesalahan jaringan: ${e.message}"
                Log.e("PenilaianViewModel", "Exception", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun simpanPenilaian(submissionId: String, juriId: String, nilai: Int, catatan: String) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            if (juriId.isBlank()) {
                _error.value = "ID Juri tidak ditemukan. Silakan login ulang."
                _isSaving.value = false
                return@launch
            }

            val requestBody = PenilaianRequest(nilai = nilai, catatan = catatan)

            try {
                val response = apiService.postPenilaian(submissionId, juriId, requestBody)
                if (response.isSuccessful) {
                    _saveSuccess.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Gagal menyimpan: ${response.code()} ${response.message()}"
                    Log.e("PenilaianViewModel", "Save Error Body: $errorBody")
                }
            } catch (e: Exception) {
                _error.value = "Terjadi kesalahan jaringan: ${e.message}"
                Log.e("PenilaianViewModel", "Save Exception", e)
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun resetSaveStatus() {
        _saveSuccess.value = false
    }
}