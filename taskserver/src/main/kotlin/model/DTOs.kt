package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
    val id: Int,
    val name: String,
    val icon: String,
    val color: Long,
    val tasks: List<TaskDto>
)

@Serializable
data class TaskDto(
    val id: Int,
    val name: String,
    val icon: String,
    val color: Long,
    val type: String,
    val isComplete: Boolean,
    val date: Long
)

@Serializable
data class UserDto(
    val login: String,
    val passwordHash: String,
    val username: String,
    val email: String
)