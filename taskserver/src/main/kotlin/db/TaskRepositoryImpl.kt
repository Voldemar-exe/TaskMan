package com.example.db

import com.example.db.DatabaseFactory.suspendTransaction
import com.example.db.dao.GroupTaskDAO
import com.example.db.dao.TaskDAO
import com.example.db.dao.UserDAO
import com.example.db.dao.UserTaskDAO
import com.example.db.dao.taskDaoToDto
import com.example.db.tables.GroupTaskTable
import com.example.db.tables.TasksTable
import com.example.db.tables.UserTaskTable
import com.example.shared.dto.TaskDto
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll

class TaskRepositoryImpl : TaskRepository {

    override suspend fun allTasks(login: String): List<TaskDto> = suspendTransaction {
        UserDAO.findById(login)?.let { userRow ->
            (UserTaskTable innerJoin TasksTable).selectAll()
                .where { UserTaskTable.login eq userRow.id }
                .map {
                    taskDaoToDto(TaskDAO.wrapRow(it))
                }
        } ?: emptyList()
    }

    override suspend fun addTask(login: String, newTask: TaskDto): Boolean = suspendTransaction {
        UserDAO.findById(login)?.let { userDao ->
            TaskDAO.new {
                name = newTask.name
                icon = newTask.icon
                color = newTask.color
                type = newTask.type
                note = newTask.note
                isComplete = newTask.isComplete
                date = newTask.date
            }.also {
                UserTaskDAO.new {
                    this.login = userDao
                    this.taskId = it
                }
                true
            }
        }
        false
    }

    override suspend fun updateTask(login: String, task: TaskDto): Boolean = suspendTransaction {
        UserTaskDAO.find {
            (UserTaskTable.login eq login) and (UserTaskTable.taskId eq task.id)
        }.firstOrNull()?.let {
            TaskDAO.findByIdAndUpdate(task.id) {
                it.name = task.name
                it.icon = task.icon
                it.color = task.color
                it.type = task.type
                it.note = task.note
                it.isComplete = task.isComplete
                it.date = task.date
            }?.let {
                true
            }
            false
        }
        false
    }

    override suspend fun deleteTask(login: String, taskId: Int): Boolean = suspendTransaction {
        UserTaskDAO.find {
            (UserTaskTable.login eq login) and (UserTaskTable.taskId eq taskId)
        }.firstOrNull()?.let { userTaskDao ->
            TaskDAO.findById(taskId)?.let {
                GroupTaskDAO.find { GroupTaskTable.taskId eq taskId }.forEach {
                    it.delete()
                }
                userTaskDao.delete()
                it.delete()
                true
            }
        }
        false
    }
}
