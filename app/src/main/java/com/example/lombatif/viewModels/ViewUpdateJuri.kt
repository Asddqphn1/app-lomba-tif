package com.example.lombatif.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.request.RequestUpdateJuri
import kotlinx.coroutines.launch

class ViewUpdateJuri : ViewModel() {
    var isLoading by mutableStateOf(false)
        private set

    var message by mutableStateOf<String?>(null)
        private set

    fun updateToJuri(userId: String, request: RequestUpdateJuri, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            message = null

            try {
                val response = Retrofins.api.updateUsertoJuri(userId, request)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        message = body.message
                        onSuccess()
                        Log.d("TESTING", "Tombol ditekan, userId: $userId, request: $request")

                    } else {
                        message = body?.message ?: "Gagal menjadikan juri"
                    }
                } else {
                    message = "HTTP ${response.code()}"
                }

            } catch (e: Exception) {
                Log.e("ViewUpdateJuri", "Error updating user to judge", e) // 💥 Log error
                message = e.message ?: "Terjadi kesalahan"
            } finally {
                isLoading = false
            }
        }
    }

}