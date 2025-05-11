package com.example.taskman.api.auth

import com.example.shared.request.LoginRequest
import com.example.shared.request.RegisterRequest
import com.example.shared.response.LoginResponse
import com.example.shared.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @DELETE("users/{login}")
    suspend fun deleteUser(@Path("login") login: String): Response<Unit>
}
