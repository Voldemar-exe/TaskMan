package com.example.shared.dto

import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: Int,
    val name: String,
    val icon: Int,
    val color: Long,
    val type: String,
    val isComplete: Boolean,
    val date: Long
)