package com.example.network

import com.example.shared.UserGroupWithTasks
import com.example.shared.UserTask
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val login: String,
    val password: String,
)

@Serializable
data class RegisterRequest(
    val login: String,
    val password: String,
    val username: String,
    val email: String,
    val tasksWithoutGroup: List<UserTask>,
    val groupsWithTasks: List<UserGroupWithTasks>
)

@Serializable
data class SyncRequest<T>(
    val entitiesToUpdate: List<T>,
    val allEntitiesIds: List<Int>
)