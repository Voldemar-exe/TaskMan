package com.example.shared

data class SharedTask(
    override val localId: Int,
    override val serverId: Int?,
    override val name: String,
    override val icon: ItemIcon,
    override val color: ItemColor,
    override val type: String,
    override val isComplete: Boolean,
    override val date: Long
): UserTask() {
    override fun updateIsSynced(isSynced: Boolean): SharedTask {
        return this.updateIsSynced(isSynced = isSynced)
    }

    override fun toggleCompletion(): SharedTask {
        return this.copy(isComplete = !this.isComplete)
    }

    override fun updateServerId(serverId: Int): SharedTask {
        return this.copy(serverId = serverId)
    }
}