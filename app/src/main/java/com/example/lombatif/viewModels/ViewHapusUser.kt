package com.example.lombatif.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class ViewHapusUser : ViewModel() {
    fun hapusUserById(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        Log.d("ViewHapusUser", "Mencoba hapus user dengan id: $id")
        viewModelScope.launch {
            try {
                Retrofins.api.hapusUser(id = id)
                onSuccess()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val message = try {
                    JSONObject(errorBody ?: "").getString("message")
                } catch (e: Exception) {
                    e.message ?: "Gagal hapus"
                }
                onError("Gagal hapus: $message")
            }
        }
    }
}