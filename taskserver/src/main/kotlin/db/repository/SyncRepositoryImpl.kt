package com.example.db.repository

import com.example.db.DatabaseFactory.suspendTransaction
import com.example.db.dao.GroupDAO
import com.example.db.dao.GroupTaskDAO
import com.example.db.dao.TaskDAO
import com.example.db.dao.UserDAO
import com.example.db.dao.UserGroupDAO
import com.example.db.dao.UserTaskDAO
import com.example.db.dao.groupDaoTpDto
import com.example.db.dao.taskDaoToDto
import com.example.db.tables.GroupTaskTable
import com.example.db.tables.TasksTable
import com.example.db.tables.UserGroupTable
import com.example.db.tables.UserTaskTable
import com.example.db.tables.UsersTable
import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto

class SyncRepositoryImpl : SyncRepository {
    override suspend fun syncTasks(
        login: String,
        tasks: List<TaskDto>,
        allTasksIds: List<Int>
    ): Result<List<TaskDto>> = suspendTransaction {
        val user = UserDAO.find { UsersTable.login eq login }.singleOrNull()
            ?: return@suspendTransaction Result.failure(IllegalStateException("User not found"))

        val updatedTasks = mutableListOf<TaskDto>()

        tasks.forEach { taskDto ->
            val task = TaskDAO.findByIdAndUpdate(taskDto.id) {
                it.name = taskDto.name
                it.icon = taskDto.icon
                it.color = taskDto.color
                it.type = taskDto.type
                it.isComplete = taskDto.isComplete
                it.date = taskDto.date
            } ?: TaskDAO.new {
                this.name = taskDto.name
                this.icon = taskDto.icon
                this.color = taskDto.color
                this.type = taskDto.type
                this.isComplete = taskDto.isComplete
                this.date = taskDto.date
            }.also {
                UserTaskDAO.new {
                    this.login = user
                    this.taskId = it
                }
            }
            updatedTasks.add(taskDaoToDto(task))
        }

        // REMOVE DELETED TASKS
        UserTaskDAO.find { UserTaskTable.login eq login }
            .filter { it.taskId.id.value !in (allTasksIds + updatedTasks.map { it.id }) }
            .forEach {
                it.delete()
                TaskDAO
                    .find { TasksTable.id eq it.id }
                    .forEach { it.delete() }
            }

        Result.success(updatedTasks)
    }

    override suspend fun syncGroups(
        login: String,
        groups: List<GroupDto>,
        allGroupsIds: List<Int>
    ): Result<List<GroupDto>> = suspendTransaction {
        val user = UserDAO.find { UsersTable.login eq login }.singleOrNull()
            ?: return@suspendTransaction Result.failure(error("User not found"))

        val updatedGroups = mutableListOf<GroupDto>()

        groups.forEach { groupDto ->
            val group = GroupDAO.findByIdAndUpdate(groupDto.id) {
                it.name = groupDto.name
                it.icon = groupDto.icon
                it.color = groupDto.color
            }?.also { group ->
                val currentLinks: List<Int> =
                    GroupTaskDAO.find { GroupTaskTable.groupId eq group.id }
                        .map { it.taskId.id.value }

                groupDto.tasks.forEach { task ->
                    currentLinks.find { it == task.id } ?: GroupTaskDAO.new {
                        this.groupId = group
                        this.taskId = TaskDAO.findById(task.id)
                            ?: error("Task in group don't exist")
                    }
                }

                // REMOVE DELETED TASKS FROM GROUPS
                GroupTaskDAO.find { GroupTaskTable.groupId eq group.id }
                    .filter { it.taskId.id.value !in groupDto.tasks.map { it.id } }
                    .forEach { it.delete() }
            } ?: GroupDAO.new {
                this.name = groupDto.name
                this.icon = groupDto.icon
                this.color = groupDto.color
            }.also { group ->
                UserGroupDAO.new {
                    this.login = user
                    this.groupId = group
                }
                groupDto.tasks.forEach { task ->
                    GroupTaskDAO.new {
                        this.groupId = group
                        this.taskId = TaskDAO.findById(task.id)!!
                    }
                }
            }
            updatedGroups.add(groupDaoTpDto(group).copy(tasks = groupDto.tasks))
        }

        // REMOVE DELETED GROUPS
        UserGroupDAO.find { UserGroupTable.login eq user.login }
            .filter { it.groupId.id.value !in (allGroupsIds + updatedGroups.map { it.id }) }
            .forEach { userGroupDao ->
                userGroupDao.delete()
                GroupTaskDAO
                    .find { GroupTaskTable.groupId eq userGroupDao.groupId.id }
                    .forEach { it.delete() }
                userGroupDao.groupId.delete()
            }

        Result.success(updatedGroups)
    }
}
