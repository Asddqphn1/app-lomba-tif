package com.example.lombatif.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ViewHapusLomba : ViewModel() {
        fun hapusLombaById(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
            viewModelScope.launch {
                try {
                    Retrofins.api.hapusLomba(id = id)
                    onSuccess()
                } catch (e: IOException) {
                    onError("Koneksi gagal: ${e.message}")
                } catch (e: HttpException) {
                    onError("Gagal hapus: ${e.message}")
                } catch (e: Exception) {
                    onError("Terjadi kesalahan: ${e.message}")
                }
            }
        }
    }