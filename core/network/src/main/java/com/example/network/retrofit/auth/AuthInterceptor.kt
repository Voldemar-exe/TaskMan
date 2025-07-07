package com.example.network.retrofit.auth

import android.util.Log
import com.example.data.repository.SessionRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionRepository: SessionRepository
) : Interceptor {
    companion object {
        private const val TAG = "AuthInterceptor"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = runBlocking {
            sessionRepository.getProfileData()?.token
        }

        Log.d(TAG, "Request URL: ${original.url}")
        Log.d(TAG, "Token present: ${!token.isNullOrBlank()}")
        Log.d(TAG, "Token prefix: ${token?.take(10)}")

        val request = original.newBuilder().apply {
            if (!token.isNullOrBlank()) {
                Log.d(TAG, "Adding Authorization header with Bearer token")
                addHeader("Authorization", "Bearer $token")
            } else {
                Log.w(TAG, "No token available for request")
            }
        }.build()

        Log.d(TAG, "Final request headers: ${request.headers}")

        return chain.proceed(request).also { response ->
            Log.d(TAG, "Response code: ${response.code}")
            Log.d(TAG, "Response message: ${response.message}")
            if (!response.isSuccessful) {
                Log.e(TAG, "Request failed with code ${response.code}")
            } else {
                Log.i(TAG, "Request success with response $response")
            }
        }
    }
}
