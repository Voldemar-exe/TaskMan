package com.example.shared.dto

import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
    val id: Int,
    val name: String,
    val icon: String,
    val color: Long,
    val tasks: List<TaskDto>
)