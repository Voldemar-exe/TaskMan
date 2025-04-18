package com.example.taskman.ui.auth

import android.util.Log
import com.google.gson.annotations.SerializedName


data class RegisterRequest(
    @SerializedName("login") val login: String,
    @SerializedName("password") val password: String
)

data class LoginRequest(
    @SerializedName("login") val login: String,
    @SerializedName("password") val password: String
)

data class LoginResponseRemote(
    @SerializedName("token") val token: String
)

private const val TAG = "AuthService"

class AuthService {

    private val apiService = AuthClient.instance

    suspend fun registerUser(login: String, password: String): String? {
        return try {
            val response = apiService.register(RegisterRequest(login, password))
            if (response.isSuccessful) response.body()?.token else null
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            null
        }
    }

    suspend fun loginUser(login: String, password: String): String? {
        return try {
            val response = apiService.login(LoginRequest(login, password))
            if (response.isSuccessful) response.body()?.token else null
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            null
        }
    }
}