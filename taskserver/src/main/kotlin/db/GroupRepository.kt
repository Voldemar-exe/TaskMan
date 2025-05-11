package com.example.db

import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto

interface GroupRepository {
    suspend fun getGroupsForUser(login: String): List<GroupDto>
    suspend fun getGroupTasks(login: String, groupId: Int): List<TaskDto>
    suspend fun createGroup(login: String, group: GroupDto): Int
    suspend fun syncTasksForGroup(login: String, groupId: Int, tasks: List<Int>): Boolean
    suspend fun updateGroup(login: String, group: GroupDto): Boolean
    suspend fun deleteGroup(login: String, groupId: Int): Boolean
}