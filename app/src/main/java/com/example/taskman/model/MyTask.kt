package com.example.taskman.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared.dto.TaskDto

@Entity(tableName = "tasks")
data class MyTask(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    val serverId: Int?,
    val name: String,
    val icon: Int,
    val color: Long,
    val type: String,
    val note: String,
    val isComplete: Boolean,
    val date: Long
) {
    constructor(dto: TaskDto) : this(
        serverId = dto.id,
        name = dto.name,
        icon = dto.icon,
        color = dto.color,
        type = dto.type,
        note = dto.note,
        isComplete = dto.isComplete,
        date = dto.date
    )
}
