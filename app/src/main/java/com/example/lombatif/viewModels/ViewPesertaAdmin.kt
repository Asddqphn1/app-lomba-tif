package com.example.lombatif.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.get.PesertaAdmin
import kotlinx.coroutines.launch
import retrofit2.Response
import com.example.lombatif.response.ResponsePesertaAdmin

class ViewPesertaAdmin : ViewModel() {
    var pesertaList by mutableStateOf<List<PesertaAdmin>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadPeserta(jenis: String? = null) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response: Response<ResponsePesertaAdmin> = Retrofins.api.fetchPeserta(jenis)
                if (response.isSuccessful) {
                    val body = response.body()
                    pesertaList = body?.data ?: emptyList()
                    errorMessage = null
                } else {
                    errorMessage = "Gagal: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Terjadi kesalahan tak dikenal"
            } finally {
                isLoading = false
            }
        }
    }
}
