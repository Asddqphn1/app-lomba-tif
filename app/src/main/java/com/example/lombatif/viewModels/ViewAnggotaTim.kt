package com.example.lombatif.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.get.AnggotaData
import kotlinx.coroutines.launch

class ViewAnggotaTim : ViewModel() {

    var anggota by mutableStateOf<List<AnggotaData>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadAnggotaTim(id: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = Retrofins.api.fetchAnggotaTim(id)
                if (response.isSuccessful) {
                    anggota = response.body()?.data?.anggotaTim ?: emptyList()
                } else {
                    errorMessage = "Gagal: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage ?: "Tidak diketahui"}"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() {
        anggota = emptyList()
        errorMessage = null
        isLoading = false
    }
}
