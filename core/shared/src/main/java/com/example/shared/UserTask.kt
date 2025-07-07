package com.example.shared

import kotlinx.serialization.Serializable

@Serializable
abstract class UserTask : ServerEntity(), Syncable<UserTask> {
    abstract val icon: ItemIcon
    abstract val color: ItemColor
    abstract val type: String
    abstract val isComplete: Boolean
    abstract val date: Long
    abstract fun toggleCompletion(): UserTask
}

