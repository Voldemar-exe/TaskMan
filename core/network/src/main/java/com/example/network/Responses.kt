package com.example.network

import com.example.shared.UserGroupWithTasks
import com.example.shared.UserTask
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val username: String,
    val email: String,
    val tasks: List<UserTask>,
    val groups: List<UserGroupWithTasks>
)

@Serializable
data class RegisterResponse(
    val token: String,
    val tasks: List<UserTask>,
    val groups: List<UserGroupWithTasks>
)

@Serializable
data class SyncResponse<T>(
    val updatedEntities: List<T>
)