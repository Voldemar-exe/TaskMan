package com.example.taskman.api.task

import com.example.taskman.model.MyTask
import kotlinx.serialization.Serializable

@Serializable
data class TaskRequest(
    val id: Int,
    val name: String,
    val icon: Int,
    val color: Long,
    val type: String,
    val note: String,
    val isComplete: Boolean,
    val date: Long
) {
    constructor(myTask: MyTask) : this(
        id = myTask.serverId ?: 0,
        name = myTask.name,
        icon = myTask.icon,
        color = myTask.color,
        type = myTask.type,
        note = myTask.note,
        isComplete = myTask.isComplete,
        date = myTask.date
    )
}