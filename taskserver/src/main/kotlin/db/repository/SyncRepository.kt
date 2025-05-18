package com.example.db.repository

import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto

interface SyncRepository {
    suspend fun syncTasks(
        login: String,
        tasks: List<TaskDto>,
        allTaskIds: List<Int>
    ): Result<List<TaskDto>>

    suspend fun syncGroups(
        login: String,
        groups: List<GroupDto>,
        allGroupsIds: List<Int>
    ): Result<List<GroupDto>>
}