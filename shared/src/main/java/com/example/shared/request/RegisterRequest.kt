package com.example.shared.request

import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val password: String,
    val username: String,
    val email: String,
    val tasksWithoutGroup: List<TaskDto>,
    val groupsWithTasks: List<GroupDto>
)