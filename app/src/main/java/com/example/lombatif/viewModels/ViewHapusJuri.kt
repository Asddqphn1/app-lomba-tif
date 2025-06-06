package com.example.lombatif.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ViewHapusJuri : ViewModel() {
    fun hapusJuriById(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        Log.d("ViewHapusUser", "Mencoba hapus user dengan id: $id")
        viewModelScope.launch {
            try {
                Retrofins.api.hapusJuri(id = id)
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