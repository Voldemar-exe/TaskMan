package com.example.shared.request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val password: String,
    val username: String,
    val email: String
)