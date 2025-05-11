package com.example.shared.response

import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto
import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(
    val token: String,
    val tasks: List<TaskDto>,
    val groups: List<GroupDto>
)
