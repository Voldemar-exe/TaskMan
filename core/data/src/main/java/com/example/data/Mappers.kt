package com.example.data

import com.example.database.model.GroupWithTasks
import com.example.database.model.MyTask
import com.example.database.model.TaskGroup
import com.example.shared.UserGroupWithTasks
import com.example.shared.UserTask
import com.example.shared.UserTaskGroup

fun UserTask.toMyTask() = MyTask(
    localId = this.localId,
    serverId = this.serverId,
    name = this.name,
    icon = this.icon,
    color = this.color,
    type = this.type,
    isComplete = this.isComplete,
    date = this.date
)

fun UserTaskGroup.toTaskGroup() = TaskGroup(
    localId = this.localId,
    serverId = this.serverId,
    name = this.name,
    icon = this.icon,
    color = this.color
)

fun UserGroupWithTasks.toGroupWithTasks() = GroupWithTasks(
    group = this.group.toTaskGroup(),
    tasks = this.tasks.map { it.toMyTask() }
)