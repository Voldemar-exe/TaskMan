package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val username: String,
    val email: String,
    val tasks: List<TaskDto>,
    val groups: List<GroupDto>
)

@Serializable
data class RegisterResponse(
    val token: String,
    val tasks: List<TaskDto>,
    val groups: List<GroupDto>
)

@Serializable
data class SyncResponse<T>(
    val updatedEntities: List<T>
)