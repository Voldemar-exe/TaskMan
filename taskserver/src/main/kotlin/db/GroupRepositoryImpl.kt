package com.example.db

import com.example.db.DatabaseFactory.suspendTransaction
import com.example.db.dao.GroupDAO
import com.example.db.dao.GroupTaskDAO
import com.example.db.dao.TaskDAO
import com.example.db.dao.UserDAO
import com.example.db.dao.UserGroupDAO
import com.example.db.dao.groupDaoTpDto
import com.example.db.dao.taskDaoToDto
import com.example.db.tables.GroupTaskTable
import com.example.db.tables.GroupsTable
import com.example.db.tables.TasksTable
import com.example.db.tables.UserGroupTable
import com.example.shared.dto.GroupDto
import com.example.shared.dto.TaskDto
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll

class GroupRepositoryImpl : GroupRepository {
    override suspend fun getGroupsForUser(login: String): List<GroupDto> = suspendTransaction {
        UserDAO.findById(login)?.let { userDao ->
            (UserGroupTable innerJoin GroupsTable).selectAll()
                .where { UserGroupTable.login eq userDao.id }
                .map {
                    groupDaoTpDto(GroupDAO.wrapRow(it))
                }
        } ?: emptyList()
    }

    override suspend fun getGroupTasks(
        login: String,
        groupId: Int
    ): List<TaskDto> = suspendTransaction {
        UserDAO.findById(login)?.let { userDao ->
            (GroupTaskTable innerJoin TasksTable).selectAll()
                .where { GroupTaskTable.groupId eq groupId }
                .map {
                    taskDaoToDto(TaskDAO.wrapRow(it))
                }
        } ?: emptyList()
    }

    override suspend fun createGroup(
        login: String,
        group: GroupDto
    ): Int = suspendTransaction {

        GroupDAO.new {
            name = group.name
            icon = group.icon
            color = group.color
        }.let { groupDao ->
            UserDAO.findById(login)?.let { userRow ->
                UserGroupDAO.new {
                    this.login = userRow
                    this.groupId = groupDao
                }
            }
            groupDao.id.value
        }
    }

    override suspend fun syncTasksForGroup(
        login: String,
        groupId: Int,
        taskIds: List<Int>
    ): Boolean = suspendTransaction {

        val user = UserDAO.findById(login) ?: error("User not found")
        val group = (UserGroupTable innerJoin GroupsTable).selectAll()
            .where { UserGroupTable.login eq user.id }
            .map {
                GroupDAO.wrapRow(it)
            }.find { it.id.value == groupId } ?: error("Group not found")


        val currentLinks = GroupTaskDAO.find { GroupTaskTable.groupId eq group.id }
        val currentTaskIds = currentLinks.map { it.taskId.id.value }

        // REMOVE TASKS FROM GROUP
        currentLinks.filter { it.taskId.id.value !in taskIds }.forEach { it.delete() }

        // ADD TASKS TO GROUP
        taskIds.filter { it !in currentTaskIds }.forEach { taskId ->
            GroupTaskDAO.new {
                this.groupId = group
                this.taskId = TaskDAO.findById(taskId)!!
            }
        }

        true
    }

    override suspend fun updateGroup(
        login: String,
        group: GroupDto
    ): Boolean = suspendTransaction {

        UserGroupDAO.find {
            (UserGroupTable.login eq login) and (UserGroupTable.groupId eq group.id)
        }.firstOrNull()?.let {
            GroupDAO.findByIdAndUpdate(group.id) {
                it.name = group.name
                it.icon = group.icon
                it.color = group.color
            }?.let {
                true
            }
            false
        }
        false
    }

    override suspend fun deleteGroup(login: String, groupId: Int): Boolean = suspendTransaction {
        UserGroupDAO.find {
            (UserGroupTable.login eq login) and (UserGroupTable.groupId eq groupId)
        }.firstOrNull()?.let { userGroupDao ->
            GroupDAO.findById(groupId)?.let {
                GroupTaskDAO.find { GroupTaskTable.groupId eq groupId }.forEach {
                    it.delete()
                }
                userGroupDao.delete()
                it.delete()
                true
            }
        }
        false
    }
}