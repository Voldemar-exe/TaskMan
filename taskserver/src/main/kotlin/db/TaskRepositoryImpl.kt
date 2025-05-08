package com.example.db

import com.example.db.DatabaseFactory.suspendTransaction
import com.example.db.tables.GroupTaskTable
import com.example.db.tables.TaskDAO
import com.example.db.tables.TasksTable
import com.example.db.tables.UserDAO
import com.example.db.tables.UserTaskDAO
import com.example.db.tables.UserTaskTable
import com.example.db.tables.taskDaoToDto
import com.example.dto.request.TaskDto
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

class TaskRepositoryImpl : TaskRepository {

    override suspend fun allTasks(login: String): List<TaskDto> = suspendTransaction {
        UserDAO.findById(login)?.let { userRow ->
            (UserTaskTable innerJoin TasksTable).select {
                UserTaskTable.user eq userRow.id
            }.map {
                taskDaoToDto(TaskDAO.wrapRow(it))
            }
        } ?: emptyList()
    }

    override suspend fun addTask(login: String, newTask: TaskDto): Boolean = suspendTransaction {
        TaskDAO.new {
            name = newTask.name
            icon = newTask.icon
            color = newTask.color
            type = newTask.type
            note = newTask.note
            isComplete = newTask.isComplete
            date = newTask.date
        }.let { taskDAO ->
            UserDAO.findById(login)?.let { userRow ->
                UserTaskDAO.new {
                    user = userRow
                    task = taskDAO
                }.id.value > 0
            } == true
        }
    }

    override suspend fun addTaskToGroup(login: String, groupId: Int, taskId: Int) {
        suspendTransaction {
            GroupTaskTable.insert {
                it[GroupTaskTable.groupId] = groupId
                it[GroupTaskTable.taskId] = taskId
            }
        }
    }

    override suspend fun updateTask(login: String, task: TaskDto) {
        suspendTransaction {
            UserDAO.findById(login)?.let {
                TasksTable.update {
                    it[name] = task.name
                    it[icon] = task.icon
                    it[color] = task.color
                    it[type] = task.type
                    it[note] = task.note
                    it[isComplete] = task.isComplete
                    it[date] = task.date
                }
            }
        }
    }

    override suspend fun removeTask(login: String, id: Int): Boolean {
        return suspendTransaction {
            UserDAO.findById(login)?.let {
                UserTaskTable.deleteWhere { task eq id }
                TaskDAO.findById(id)?.delete() ?: false
                true
            }
            false
        }
    }
}