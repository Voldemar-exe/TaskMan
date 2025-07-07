package com.example.shared

import kotlinx.serialization.Serializable

@Serializable
abstract class UserTaskGroup: ServerEntity(), Syncable<UserTaskGroup> {
    abstract val icon: ItemIcon
    abstract val color: ItemColor
}

