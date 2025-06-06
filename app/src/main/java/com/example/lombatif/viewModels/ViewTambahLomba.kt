package com.example.lombatif.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.request.RequestLomba
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class ViewTambahLomba : ViewModel() {
    private val _submitState = MutableStateFlow<SubmitStatus>(SubmitStatus.Idle)
    val submitState: StateFlow<SubmitStatus> = _submitState

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun submitLomba(lomba: RequestLomba) {
        viewModelScope.launch {
            _submitState.value = SubmitStatus.Loading

            try {
                val response = Retrofins.api.PostLomba(lomba)
                if (response.isSuccessful) {
                    _submitState.value = SubmitStatus.Success
                    _successMessage.value = "Berhasil menambahkan lomba"
                    Log.d("ViewTambahLomba", "Submit lomba: $lomba")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = try {
                        val json = JSONObject(errorBody ?: "")
                        json.getString("message")
                    } catch (e: Exception) {
                        "Gagal: ${response.code()}"
                    }

                    Log.e("SubmitLomba", "Submit failed: $errorMessage")
                    _submitState.value = SubmitStatus.Error(errorMessage)
                }
            } catch (e: IOException) {
                _submitState.value = SubmitStatus.Error("Network error")
            } catch (e: HttpException) {
                _submitState.value = SubmitStatus.Error("Server error")
            }
        }
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}

// State untuk hasil submit
sealed class SubmitStatus {
    object Idle : SubmitStatus()
    object Loading : SubmitStatus()
    object Success : SubmitStatus()
    data class Error(val message: String) : SubmitStatus()
}
