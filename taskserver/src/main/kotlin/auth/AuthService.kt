package com.example.auth

import com.example.shared.request.LoginRequest
import com.example.shared.request.RegisterRequest
import com.example.shared.response.LoginResponse
import com.example.shared.response.RegisterResponse

interface AuthService {
    suspend fun register(credentials: RegisterRequest): Result<RegisterResponse>
    suspend fun login(credentials: LoginRequest): Result<LoginResponse>
}