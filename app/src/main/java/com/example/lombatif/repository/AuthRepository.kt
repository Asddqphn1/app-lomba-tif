package com.example.lombatif.repository

import com.example.lombatif.api.ApiService
import com.example.lombatif.response.LoginReq
import com.example.lombatif.response.UserData

class AuthRepository(private val api: ApiService) {
    suspend fun login(email: String, password: String): Result<UserData> {
        return try {
            val response = api.login(LoginReq(email, password))
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.data
                // Cookie HttpOnly sudah otomatis disimpan via CookieJar
                Result.success(user)
            } else {
                Result.failure(Exception("Login gagal: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
