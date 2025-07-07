package com.example.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shared.ItemColor
import com.example.shared.ItemIcon
import com.example.shared.UserTaskGroup

@Entity(tableName = "groups")
data class TaskGroup(
    @PrimaryKey(autoGenerate = true) override val localId: Int = 0,
    override val serverId: Int?,
    override val name: String,
    override val icon: ItemIcon,
    override val color: ItemColor,
    val isSynced: Boolean = false
): UserTaskGroup() {
    override fun updateIsSynced(isSynced: Boolean): TaskGroup {
        return this.updateIsSynced(isSynced = isSynced)
    }

    override fun updateServerId(serverId: Int): TaskGroup {
        return this.copy(serverId = serverId)
    }
}