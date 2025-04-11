package com.example.auth

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReceiveRemote(
    val login: String,
    val password: String,
    val username: String,
    val email: String
)

@Serializable
data class RegisterResponseRemote(
    val token: String
)