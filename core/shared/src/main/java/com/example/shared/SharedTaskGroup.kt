package com.example.shared

data class SharedTaskGroup(
    override val localId: Int,
    override val serverId: Int?,
    override val name: String,
    override val icon: ItemIcon,
    override val color: ItemColor
): UserTaskGroup() {
    override fun updateIsSynced(isSynced: Boolean): SharedTaskGroup {
        return this.updateIsSynced(isSynced = isSynced)
    }

    override fun updateServerId(serverId: Int): SharedTaskGroup {
        return this.copy(serverId = serverId)
    }
}