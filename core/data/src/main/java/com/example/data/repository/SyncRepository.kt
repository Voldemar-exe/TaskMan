package com.example.data.repository

import com.example.data.toGroupWithTasks
import com.example.data.toMyTask
import com.example.database.dao.SyncDao
import com.example.shared.UserGroupWithTasks
import com.example.shared.UserTask
import jakarta.inject.Inject

class SyncRepository @Inject constructor(private val syncDao: SyncDao) {

    suspend fun updateGroupsWithTaskFromServer(groups: List<UserGroupWithTasks>) {
        syncDao.updateGroupsWithTaskFromServer(groups.map { it.toGroupWithTasks() })
    }

    suspend fun updateTasksByDataFromServer(tasks: List<UserTask>) {
        syncDao.updateTasksByDataFromServer(tasks.map { it.toMyTask() })
    }

}