package com.example.shared

import kotlinx.serialization.Serializable

@Serializable
abstract class UserGroupWithTasks {
    abstract val group: UserTaskGroup
    abstract val tasks: List<UserTask>
}