package com.example.lombatif.viewModels

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.request.RequestRegister
import kotlinx.coroutines.launch
import kotlin.math.log

class ViewRegisterUser : ViewModel() {
    var stateUI by mutableStateOf("")


    fun validasi(email: String, password: String) : Boolean{
        var isValid = true
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            isValid = false
            stateUI = "Email tidak valid"
        }

        if (password.length < 6){
            isValid = false
            stateUI = "Password terlalu pendek"
        }

        return isValid
    }

    fun postUser(nama: String, email : String, password : String) {
        viewModelScope.launch {
            try{
                val response = Retrofins.api.setRegisterUser(RequestRegister(nama, email, password))
                if (response.isSuccessful){
                    val body = response.body()
                    stateUI = body?.message ?: "Success"
                }else{
                    val errorBody = response.errorBody()?.string()
                    stateUI = "Error ${errorBody ?: "error"}"
                }

            }catch (e : Exception){
                Log.e("Error" , e.message.toString())
            }
        }
    }
}