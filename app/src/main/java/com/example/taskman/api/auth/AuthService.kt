package com.example.taskman.api.auth

import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Response


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


class AuthService(
    private val apiClient: AuthApi
) {
    suspend fun registerUser(
        request: RegisterRequest
    ): String? {
        Log.d("AuthService", "Register: $request")
        return safeApiCall { apiClient.register(request) }?.token
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
        return safeApiCall { apiClient.login(LoginRequest(login, password)) }?.token
    }

    private suspend fun <T> safeApiCall(call: suspend () -> Response<T>): T? {
        return try {
            val response = call()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e(TAG, "API error", e)
            null
        }
    }

    companion object {
        private const val TAG = "AuthService"
    }
}