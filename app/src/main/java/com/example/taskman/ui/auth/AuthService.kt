package com.example.taskman.ui.auth

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val TAG = "AuthService"

class AuthService(private val client: HttpClient) {


    data class RegisterRequest(
        val username: String,
        val passwordHash: String
    )

    suspend fun registerUser(login: String, password: String): Boolean {
        return try {
            Log.d(TAG, "Sending registration request: login=$login")
            val response = client.post("http://10.0.2.2:8080/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(username = login, passwordHash = password))
            }
            Log.d(TAG, "Registration response: status=${response.status}")
            response.status.value in 200..299
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed", e)
            false
        }
    }

    data class LoginRequest(
        val username: String,
        val passwordHash: String
    )

    suspend fun loginUser(login: String, password: String): Boolean {
        return try {
            Log.d(TAG, "Sending login request: login=$login")
            val response = client.post("http://10.0.2.2:8080/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(username = login, passwordHash = password))
            }
            response.status.value in 200..299
        } catch (e: Exception) {
            false
        }
    }
}