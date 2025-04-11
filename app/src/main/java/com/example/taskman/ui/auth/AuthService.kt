package com.example.taskman.ui.auth

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

private const val TAG = "AuthService"
private const val SERVER_URL = "http://10.0.2.2:8080"

class AuthService(private val client: HttpClient) {

    @Serializable
    data class RegisterRequest(
        val login: String,
        val password: String
    )

    suspend fun registerUser(login: String, password: String): String? {
        return try {
            Log.d(TAG, "Sending registration request: login=$login")
            val response = client.post("$SERVER_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(login = login, password = password))
            }

            if (response.status.value in 200..299) {
                val token = response.body<LoginResponseRemote>().token
                Log.d(TAG, "Registration successful. Token: $token")
                token
            } else {
                Log.e(TAG, "Registration failed: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            null
        }
    }

    @Serializable
    data class LoginRequest(
        val login: String,
        val password: String
    )

    @Serializable
    data class LoginResponseRemote(
        val token: String
    )

    suspend fun loginUser(login: String, password: String): String? {
        return try {
            Log.d(TAG, "Sending login request: login=$login")
            val response = client.post("$SERVER_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(login = login, password = password).toString())
            }

            if (response.status.value in 200..299) {
                val token = response.body<LoginResponseRemote>().token
                Log.d(TAG, "Login successful. Token: $token")
                token
            } else {
                Log.e(TAG, "Login failed: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            null
        }
    }
}