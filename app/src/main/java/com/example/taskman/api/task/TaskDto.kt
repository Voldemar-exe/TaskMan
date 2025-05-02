package com.example.taskman.api.task

data class TaskDto(
    val id: Int,
    val name: String,
    val icon: Int,
    val color: Long,
    val type: String,
    val note: String,
    val isComplete: Boolean,
    val date: Long
)