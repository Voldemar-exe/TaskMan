package com.example.network.retrofit.auth

import com.example.network.LoginRequest
import com.example.network.LoginResponse
import com.example.network.RegisterRequest
import com.example.network.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}
