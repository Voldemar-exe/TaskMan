package com.example.taskman.api.auth

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponseRemote>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponseRemote>

    @DELETE("users/{login}")
    suspend fun deleteUser(@Path("login") login: String): Response<Unit>
}