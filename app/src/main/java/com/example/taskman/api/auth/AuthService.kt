package com.example.taskman.api.auth

import android.util.Log
import com.example.shared.request.LoginRequest
import com.example.shared.request.RegisterRequest
import com.example.shared.response.LoginResponse
import com.example.shared.response.RegisterResponse
import com.example.taskman.ui.utils.SessionRepository
import retrofit2.Response

class AuthService(
    private val apiClient: AuthApi,
    private val sessionRepository: SessionRepository
) {
    suspend fun registerUser(
        request: RegisterRequest
    ): RegisterResponse? {
        Log.d("AuthService", "Register: $request")
        return safeApiCall { apiClient.register(request) }?.let {
            sessionRepository.saveSession(
                ProfileData(
                    it.token,
                    request.login,
                    request.username
                )
            )
            it
        }
    }

    suspend fun loginUser(request: LoginRequest): LoginResponse? = safeApiCall {
        apiClient.login(request)
    }?.let {
        sessionRepository.saveSession(
            ProfileData(
                it.token,
                request.login,
                ""
            )
        )
        it
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
