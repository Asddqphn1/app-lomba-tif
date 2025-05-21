package com.example.lombatif.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.get.DaftarUsersAdmin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewUserAdmin : ViewModel() {
    val _users = MutableStateFlow<List<DaftarUsersAdmin>>(emptyList())
    val user : StateFlow<List<DaftarUsersAdmin>> = _users
    var stateUI by mutableStateOf("")

    init {
        fetchDaftarUser()
    }

    fun fetchDaftarUser() {
        viewModelScope.launch {
            try {
                val response = Retrofins.api.getUsers()
                _users.value = response.users
            } catch (e: Exception) {
                Log.e("DaftarLombaView", e.message.toString())
                stateUI = e.message.toString()
            }
        }
    }
}
