package com.example.taskman.api.auth

import android.util.Log
import com.example.shared.request.LoginRequest
import com.example.shared.request.RegisterRequest
import com.example.taskman.ui.auth.AuthStorage
import retrofit2.Response

class AuthService(
    private val apiClient: AuthApi,
    private val authDataStore: AuthStorage
) {
    suspend fun registerUser(
        request: RegisterRequest
    ): String? {
        Log.d("AuthService", "Register: $request")
        return safeApiCall { apiClient.register(request) }?.let {
            authDataStore.saveProfile(
                ProfileData(
                    it.token,
                    request.login,
                    request.username
                )
            )
            request.login
        }
    }

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

    suspend fun loginUser(request: LoginRequest): String? = safeApiCall {
        apiClient.login(request)
    }?.let {
        authDataStore.saveProfile(
            ProfileData(
                it.token,
                request.login,
                ""
            )
        )
        request.login
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