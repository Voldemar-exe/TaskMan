package com.example.shared.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)