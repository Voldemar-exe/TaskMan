package com.example.model

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
    val tasksWithoutGroup: List<TaskDto>,
    val groupsWithTasks: List<GroupDto>
)

@Serializable
data class SyncRequest<T>(
    val entitiesToUpdate: List<T>,
    val allEntitiesIds: List<Int>
)