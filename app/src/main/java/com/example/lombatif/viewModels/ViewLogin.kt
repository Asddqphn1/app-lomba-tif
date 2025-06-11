package com.example.lombatif.viewModels

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.request.RequestLogin
import com.example.lombatif.response.DataUser
import com.example.lombatif.response.ResponseErrorLogin
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class ViewLogin : ViewModel() {
    private var _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private var currentToken: String? = null

    fun resetState() {
        _loginState.value = LoginState.Idle
    }


    fun postLogin(email: String, password: String, context: Context) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = Retrofins.api.setLoginUser(RequestLogin(email, password))
                if (response.isSuccessful) {
                    response.body()?.let {
                        val token = it.token!!
                        val role = getRoleFromToken(token)
                        val id = getUserIdFromToken(token)

                        currentToken = token
                        saveId(id)
                        saveToken(token)
                        saveRole(role)

                        saveUserIdToPreferences(context, id ?: "") // <- ini penting

                        _loginState.value = LoginState.Success(
                            userData = it.data,
                            token = token,
                            role = role ?: "",
                            id = id ?: ""
                        )
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


    private fun saveId(id: String?) {
        _userId.value = id
    }

    private fun saveToken(token: String) {
        currentToken = token
        // Simpan token ke SharedPreferences/DataStore jika dibutuhkan
    }

    private fun saveRole(role: String?) {
        // Simpan role ke SharedPreferences/DataStore jika dibutuhkan
    }

    fun getRoleFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size >= 2) {
                val payload = String(
                    Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
                )
                val json = JSONObject(payload)
                json.getString("role")
            } else null
        } catch (e: Exception) {
            Log.e("TOKEN_PARSING", "Gagal parsing role: ${e.message}")
            null
        }
    }

    fun getUserIdFromToken(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size >= 2) {
                val payload = String(
                    Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
                )
                Log.d("TOKEN_DEBUG", "Payload Token: $payload")
                val json = JSONObject(payload)
                json.getString("id")
            } else null
        } catch (e: Exception) {
            Log.e("TOKEN_PARSING", "Gagal parsing ID: ${e.message}")
            null
        }
    }
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(
            val userData: DataUser,
            val token: String,
            val role: String,
            val id: String
        ) : LoginState()
        data class Error(val message: String) : LoginState()
    }

    fun saveUserIdToPreferences(context: Context, id: String) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("USER_ID", id).apply()
    }

}
