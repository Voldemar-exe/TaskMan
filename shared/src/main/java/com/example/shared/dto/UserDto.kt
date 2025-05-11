package com.example.shared.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val login: String,
    val passwordHash: String,
    val username: String?,
    val email: String?
)