package com.example.lombatif.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.get.JuriAdmin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewJuriAdmin : ViewModel() {
    private val _juri = MutableStateFlow<List<JuriAdmin>>(emptyList())
    val juri: StateFlow<List<JuriAdmin>> = _juri

    var stateUI = mutableStateOf("")

    init {
        fetchJuriAdmin()
    }

    fun fetchJuriAdmin(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val response = Retrofins.api.getJuri()
                _juri.value = response.juriAdmin
                stateUI.value = ""
            } catch (e: Exception) {
                Log.e("Fetch Juri admin view", e.message.toString())
                stateUI.value = e.message.toString()
            } finally {
                onComplete?.invoke()
            }
        }
    }
}
