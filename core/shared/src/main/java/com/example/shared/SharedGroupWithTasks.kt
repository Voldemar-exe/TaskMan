package com.example.shared

class SharedGroupWithTasks(
    override val group: UserTaskGroup,
    override val tasks: List<UserTask>
): UserGroupWithTasks()