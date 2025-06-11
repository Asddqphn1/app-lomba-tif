// File: viewModels/ViewDaftarLomba.kt
package com.example.lombatif.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.get.DaftarLomba
import com.example.lombatif.response.LombaDetailData
import com.example.lombatif.response.PendaftaranRequest
import com.example.lombatif.response.StatusResponse

import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

// State untuk proses pengambilan detail lomba
sealed interface LombaDetailState {
    object Idle : LombaDetailState
    object Loading : LombaDetailState
    data class Success(val detail: LombaDetailData) : LombaDetailState
    data class Error(val message: String) : LombaDetailState
}

// State untuk proses submit pendaftaran
sealed interface SubmitState {
    object Idle : SubmitState
    object Loading : SubmitState
    data class Success(val message: String) : SubmitState
    data class Error(val message: String) : SubmitState
    data class AlreadyRegistered(val message: String) : SubmitState
}

class ViewDaftarLomba : ViewModel() {
    private val _lomba = MutableStateFlow<List<DaftarLomba>>(emptyList())
    val lomba = _lomba.asStateFlow()

    private val _lombaDetailState = MutableStateFlow<LombaDetailState>(LombaDetailState.Idle)
    val lombaDetailState = _lombaDetailState.asStateFlow()

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState = _submitState.asStateFlow()

    var isRefreshing by mutableStateOf(false)
        private set

    var stateUI by mutableStateOf("")

    init {
        fetchDaftarLomba()
    }

    fun fetchDaftarLomba() {
        viewModelScope.launch {
            isRefreshing = true
            try {
                val response = Retrofins.api.getDaftarLomba()
                _lomba.value = response.daftarLomba
                stateUI = "Success"
            } catch (e: Exception) {
                Log.e("DaftarLombaView", e.message.toString())
                stateUI = e.message.toString()
            }
            isRefreshing = false
        }

    }

    fun selectLomba(lombaId: String) {
        viewModelScope.launch {
            _lombaDetailState.value = LombaDetailState.Loading
            try {
                val response = Retrofins.api.getLombaDetail(lombaId)
                _lombaDetailState.value = LombaDetailState.Success(response.data)
            } catch (e: Exception) {
                _lombaDetailState.value = LombaDetailState.Error(e.message ?: "Gagal memuat detail")
            }
        }
    }

    fun submitPendaftaran(userId: String, lombaId: String, request: PendaftaranRequest) {
        viewModelScope.launch {
            _submitState.value = SubmitState.Loading
            try {
                val response = Retrofins.api.postPendaftaran(userId, lombaId, request)
                _submitState.value = SubmitState.Success(response.message)
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                if (errorBody != null) {
                    try {
                        val errorResponse = Gson().fromJson(errorBody, StatusResponse::class.java)
                        if (errorResponse.status.equals("Gagal", ignoreCase = true)) {
                            _submitState.value = SubmitState.AlreadyRegistered(errorResponse.message)
                        } else {
                            _submitState.value = SubmitState.Error(errorResponse.message)
                        }
                    } catch (jsonError: Exception) {
                        _submitState.value = SubmitState.Error("Gagal mem-parsing pesan error.")
                    }
                } else {
                    _submitState.value = SubmitState.Error("Error HTTP ${e.code()}")
                }
            } catch (e: Exception) {
                _submitState.value = SubmitState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    // --- FUNGSI YANG HILANG ADA DI SINI ---
    // Fungsi untuk membersihkan state setelah dialog/sheet ditutup
    fun clearStates() {
        _lombaDetailState.value = LombaDetailState.Idle
        _submitState.value = SubmitState.Idle
    }
}