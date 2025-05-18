package com.example.taskman.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared.dto.TaskDto

@Entity(tableName = "tasks")
data class MyTask(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    val serverId: Int?,
    val name: String,
    val icon: String,
    val color: Long,
    val type: String,
    val isComplete: Boolean,
    val date: Long,
    val isSynced: Boolean = false
) {
    constructor(dto: TaskDto) : this(
        serverId = dto.id,
        name = dto.name,
        icon = dto.icon,
        color = dto.color,
        type = dto.type,
        isComplete = dto.isComplete,
        date = dto.date
    )

    fun toDto(): TaskDto = TaskDto(
        this.serverId ?: 0,
        this.name,
        this.icon,
        this.color,
        this.type,
        this.isComplete,
        this.date
    )
}
