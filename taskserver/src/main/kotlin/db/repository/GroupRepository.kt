package com.example.db.repository

import com.example.model.GroupDto
import com.example.model.TaskDto


interface GroupRepository {
    suspend fun getGroupsForUser(login: String): List<GroupDto>
    suspend fun getGroupTasks(login: String, groupId: Int): List<TaskDto>
    suspend fun createGroup(login: String, group: GroupDto): Int?
    suspend fun syncTasksForGroup(login: String, groupId: Int, tasks: List<Int>): Result<String>
    suspend fun updateGroup(login: String, group: GroupDto): Boolean
    suspend fun deleteGroup(login: String, groupId: Int): Result<String>
}