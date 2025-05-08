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

/**
 * {
 *    "id": 0,
 *    "name": "string",
 *    "icon": 0,
 *    "color": 0,
 *    "type": "string",
 *    "note": "string",
 *    "isComplete": true,
 *    "date": 0
 * }
 */

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