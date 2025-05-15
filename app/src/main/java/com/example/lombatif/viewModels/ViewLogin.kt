package com.example.lombatif.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.request.RequestLogin
import com.example.lombatif.response.DataUser
import com.example.lombatif.response.ResponseErrorLogin
import com.example.lombatif.viewModels.ViewLogin.LoginState
import com.google.gson.Gson
import kotlinx.coroutines.launch


class ViewLogin : ViewModel() {
    private var _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    var loginState: State<LoginState> = _loginState


    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val userData: DataUser, val token: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    fun postLogin(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = Retrofins.api.setLoginUser(RequestLogin(email, password))
                if (response.isSuccessful) {
                    response.body()?.let {
                        _loginState.value = LoginState.Success(
                            userData = it.data,
                            token = it.token!!
                        )
                        // Simpan token di preferences atau secure storage
                        saveToken(it.token)
                    } ?: run {
                        _loginState.value = LoginState.Error("Empty response body")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = try {
                        Gson().fromJson(errorBody, ResponseErrorLogin::class.java)
                    } catch (e: Exception) {
                        null
                    }
                    _loginState.value = LoginState.Error(
                        errorResponse?.message ?: "Login failed"
                    )
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Network error")
            }
        }
    }

    private fun saveToken(token: String) {

    }


}
