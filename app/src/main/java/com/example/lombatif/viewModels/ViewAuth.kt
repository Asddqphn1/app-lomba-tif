package com.example.lombatif.viewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import kotlinx.coroutines.launch


class ViewAuth : ViewModel() {
    private var _authMeState = mutableStateOf<String>("")
    var authMeState: State<String> = _authMeState


    // This function should be called after successful login
    fun getAuthMe() {
        viewModelScope.launch {
            try {
                val response = Retrofins.api.getAuthMe()
                _authMeState.value = response.autme.role.toString()
                Log.e("Role: ", _authMeState.value )
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            }
        }
    }
}
