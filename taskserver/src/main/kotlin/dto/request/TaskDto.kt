package com.example.dto.request

import kotlinx.serialization.Serializable

// TODO CREATE SHEARED PACKAGE FOR MODULES

@Serializable
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

data class GroupDto(
    val id: Int,
    val name: String,
    val icon: Int,
    val color: Long
)

data class GroupTaskDto(
    val groupId: Int,
    val taskId: Int
)