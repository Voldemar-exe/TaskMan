package com.example.data.repository

import com.example.data.toMyTask
import com.example.data.toTaskGroup
import com.example.database.dao.GroupDao
import com.example.database.model.GroupTaskCrossRef
import com.example.database.model.TaskGroup
import com.example.shared.ItemColor
import com.example.shared.SystemIcon
import com.example.shared.UserGroupWithTasks
import com.example.shared.UserTask
import com.example.shared.UserTaskGroup
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GroupRepository @Inject constructor(private val groupDao: GroupDao) {
    val allGroups: Flow<List<UserTaskGroup>> = groupDao.getAllGroupsFlow().map {
        listOf(
            TaskGroup(
                localId = -1,
                serverId = null,
                name = "Все", // TODO CHANGE TO RESOURCE
                icon = SystemIcon.Amount,
                color = ItemColor.Black
            )
        ) + it
    }

    val allGroupsWithTasks: Flow<List<UserGroupWithTasks>> =
        groupDao.getAllGroupsWithTasksFlow()

    val allGroupsNotSyncedFlow: Flow<List<UserGroupWithTasks>> =
        groupDao.getAllNotSyncedFlow()

    suspend fun deleteAllCrossRefsForGroup(groupId: Int) {
        groupDao.deleteAllCrossRefsForGroup(groupId)
    }

    suspend fun deleteGroupById(groupId: Int) {
        groupDao.deleteGroupById(groupId)
    }

    suspend fun insertGroupTaskCrossRef(groupId: Int, taskId: Int) {
        groupDao.insertGroupTaskCrossRef(GroupTaskCrossRef(groupId, taskId))
    }

    suspend fun getGroupById(groupId: Int): UserGroupWithTasks? {
        return groupDao.getGroupById(groupId)
    }

    suspend fun insertGroup(group: UserTaskGroup): Long {
        return groupDao.insertGroup(group.toTaskGroup())
    }

    suspend fun updateGroup(group: UserTaskGroup) {
        groupDao.updateGroup(group.toTaskGroup())
    }

    suspend fun updateGroups(groups: List<UserTaskGroup>) {
        groupDao.updateGroups(groups.map { it.toTaskGroup() })
    }

    suspend fun insertTask(task: UserTask) {
        groupDao.insertTask(task.toMyTask())
    }

    suspend fun insertCrossRef(ref: GroupTaskCrossRef) {
        groupDao.insertCrossRef(ref)
    }

    suspend fun deleteGroup(group: TaskGroup) {
        groupDao.deleteGroup(group)
    }

    suspend fun syncAllGroups() = groupDao.syncAllGroups()
}