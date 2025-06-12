// File: com/example/lombatif/viewModels/ViewKirimSertifikat.kt

package com.example.lombatif.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.request.RequestSertifikat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

// Menggunakan nama StatusSubmit yang sudah Anda miliki
sealed class StatusSubmit {
    object Idle : StatusSubmit()
    object Loading : StatusSubmit()
    data class Success(val message: String) : StatusSubmit()
    data class Error(val message: String) : StatusSubmit()
}


class ViewKirimSertifikat : ViewModel() {

    private val _submitState = MutableStateFlow<StatusSubmit>(StatusSubmit.Idle)
    val submitState: StateFlow<StatusSubmit> = _submitState

    fun kirimSertifikat(idLomba: String, sertifikat: RequestSertifikat) {
        viewModelScope.launch {
            _submitState.value = StatusSubmit.Loading
            try {
                val response = Retrofins.api.postSertifikat(idLomba, sertifikat)
                if (response.isSuccessful) {
                    val successMsg = response.body()?.message ?: "Sertifikat berhasil dikirim"
                    _submitState.value = StatusSubmit.Success(successMsg)
                    Log.d("ViewKirimSertifikat", "Sukses kirim sertifikat: $successMsg")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = try {
                        JSONObject(errorBody ?: "").getString("message")
                    } catch (e: Exception) {
                        "Gagal mengirim: Kode ${response.code()}"
                    }
                    _submitState.value = StatusSubmit.Error(errorMsg)
                    Log.e("ViewKirimSertifikat", "Gagal kirim: $errorMsg")
                }
            } catch (e: IOException) {
                _submitState.value = StatusSubmit.Error("Kesalahan jaringan. Periksa koneksi internet Anda.")
                Log.e("ViewKirimSertifikat", "Network Error", e)
            } catch (e: HttpException) {
                _submitState.value = StatusSubmit.Error("Terjadi kesalahan pada server.")
                Log.e("ViewKirimSertifikat", "HTTP Error", e)
            }
        }
    }

    // Fungsi untuk mereset state setelah pesan ditampilkan di UI
    fun resetSubmitState() {
        _submitState.value = StatusSubmit.Idle
    }
}