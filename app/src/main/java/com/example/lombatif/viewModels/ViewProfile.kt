package com.example.lombatif.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.response.ResponseProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewProfile : ViewModel() {
    private val _profile = MutableStateFlow<ResponseProfile?>(null)
    val profile: StateFlow<ResponseProfile?> = _profile



    var stateUI by mutableStateOf("")

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            try {
                val response = Retrofins.api.getProfile()
                Log.d("Fetch Profile", "Response: $response")

                _profile.value = response
            } catch (e: Exception) {
                Log.e("Fetch Profile", e.message.toString())
                stateUI = e.message.toString()
            }
        }

    }
}
