package com.example.auth

import com.example.model.LoginRequest
import com.example.model.LoginResponse
import com.example.model.RegisterRequest
import com.example.model.RegisterResponse


interface AuthService {
    suspend fun register(credentials: RegisterRequest): Result<RegisterResponse>
    suspend fun login(credentials: LoginRequest): Result<LoginResponse>
}