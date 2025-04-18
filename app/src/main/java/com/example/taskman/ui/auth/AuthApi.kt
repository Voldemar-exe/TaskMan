package com.example.taskman.ui.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponseRemote>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponseRemote>
}