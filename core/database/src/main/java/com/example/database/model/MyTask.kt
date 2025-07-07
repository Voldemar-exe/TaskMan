package com.example.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared.ItemColor
import com.example.shared.ItemIcon
import com.example.shared.UserTask

@Entity(tableName = "tasks")
data class MyTask(
    @PrimaryKey(autoGenerate = true) override val localId: Int = 0,
    override val serverId: Int? = null,
    override val name: String,
    override val icon: ItemIcon,
    override val color: ItemColor,
    override val type: String,
    override val isComplete: Boolean,
    override val date: Long,
    val isSynced: Boolean = false
): UserTask() {
    /*constructor(dto: TaskDto) : this(
        serverId = dto.id,
        name = dto.name,
        icon = dto.icon,
        color = dto.color,
        type = dto.type,
        isComplete = dto.isComplete,
        date = dto.date
    )

    fun toDto() =
        TaskDto(
            this.serverId ?: 0,
        this.name,
        this.icon,
        this.color,
        this.type,
        this.isComplete,
        this.date
    )*/
    override fun updateIsSynced(isSynced: Boolean): MyTask {
        return this.copy(isSynced = isSynced)
    }

    override fun toggleCompletion(): MyTask {
        return this.copy(isComplete = !this.isComplete)
    }

    override fun updateServerId(serverId: Int): MyTask {
        return this.copy(isSynced = true, serverId = serverId)
    }
}
