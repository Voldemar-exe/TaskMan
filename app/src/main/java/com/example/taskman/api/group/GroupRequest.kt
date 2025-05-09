package com.example.taskman.api.group

import com.example.taskman.model.TaskGroup
import kotlinx.serialization.Serializable

@Serializable
data class GroupRequest(
    val id: Int,
    val name: String,
    val icon: Int,
    val color: Long
) {
    constructor(taskGroup: TaskGroup) : this(
        id = taskGroup.groupId,
        name = taskGroup.name,
        icon = taskGroup.icon,
        color = taskGroup.color
    )
}
