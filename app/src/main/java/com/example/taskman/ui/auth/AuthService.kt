package com.example.taskman.ui.auth

import android.util.Log
import com.google.gson.annotations.SerializedName


data class RegisterRequest(
    @SerializedName("login") val login: String,
    @SerializedName("password") val password: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String
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

    private val apiClient = AuthClient.instance

    suspend fun registerUser(
        request: RegisterRequest
    ): String? {
        Log.d("AuthService", "Register: $request")
        return try {
            val response = apiClient.register(request)
            if (response.isSuccessful) response.body()?.token else null
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            null
        }
    }

    /**
     * Удаляет пользователя по логину
     * @return true если удаление успешно, false в случае ошибки
     */
    suspend fun deleteUser(login: String): Boolean {
        return try {
            Log.d(TAG, "Deleting user: $login")
            val response = apiClient.deleteUser(login)
            if (response.isSuccessful) {
                Log.d(TAG, "User $login deleted successfully")
                true
            } else {
                Log.e(TAG, "Failed to delete user $login: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user $login", e)
            false
        }
    }

    suspend fun loginUser(login: String, password: String): String? {
        return try {
            val response = apiClient.login(LoginRequest(login, password))
            if (response.isSuccessful) response.body()?.token else null
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            null
        }
    }
}