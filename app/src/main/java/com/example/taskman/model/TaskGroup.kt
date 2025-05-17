package com.example.taskman.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared.dto.GroupDto

@Entity(tableName = "groups")
data class TaskGroup(
    @PrimaryKey(autoGenerate = true) val groupId: Int = 0,
    val serverId: Int?,
    val name: String,
    val icon: Int,
    val color: Long,
    val isSynced: Boolean = false
) {
    constructor(dto: GroupDto) : this(
        serverId = dto.id,
        name = dto.name,
        icon = dto.icon,
        color = dto.color
    )
}