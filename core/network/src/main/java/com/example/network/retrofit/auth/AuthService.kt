package com.example.network.retrofit.auth

import android.util.Log
import com.example.data.repository.SessionRepository
import com.example.network.LoginRequest
import com.example.network.LoginResponse
import com.example.network.RegisterRequest
import com.example.network.RegisterResponse
import com.example.shared.ProfileData
import jakarta.inject.Inject
import retrofit2.Response


class AuthService @Inject constructor(
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
                    request.username,
                    request.email
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
                it.username,
                it.email
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
