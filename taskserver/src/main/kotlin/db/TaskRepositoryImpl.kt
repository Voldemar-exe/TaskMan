package com.example.db

import com.example.db.DatabaseFactory.suspendTransaction
import com.example.db.tables.GroupTaskTable
import com.example.db.tables.TasksTable
import com.example.db.tables.UserTaskTable
import com.example.dto.request.TaskDto
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class TaskRepositoryImpl : TaskRepository {

    override suspend fun allTasks(login: String): List<TaskDto> = suspendTransaction {
        TasksTable.selectAll().map {
            it.toTask()
        }
    }

    override suspend fun addTask(login: String, task: TaskDto): Boolean = suspendTransaction {
        // TODO INSERT ALSO IN USER_TASK
        //
        val rowsAffected = TasksTable.insert {
            it[taskId] = task.id
            it[name] = task.name
            it[icon] = task.icon
            it[color] = task.color
            it[type] = task.type
            it[note] = task.note
            it[isComplete] = task.isComplete
            it[date] = task.date
        }
//            .resultedValues?.let { result ->
//                UserTaskTable.insert {
//                it[login] = login
//                it[taskId] = result.first()
//            }
//        }
        rowsAffected.insertedCount > 0
    }

    override suspend fun addTaskToGroup(login: String, groupId: Int, taskId: Int) {
        suspendTransaction {
            GroupTaskTable.insert {
                it[this.groupId] = groupId
                it[this.taskId] = taskId
            }
        }
    }

    override suspend fun updateTask(login: String, task: TaskDto) {
        suspendTransaction {
            TasksTable.update({ TasksTable.taskId eq task.id }) {
                it[taskId] = task.id
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

    override suspend fun removeTask(login: String, id: Int): Boolean {
        return suspendTransaction {
            UserTaskTable.deleteWhere { taskId eq id and (UserTaskTable.login eq login) } > 0
        }
    }

    private fun ResultRow.toTask(): TaskDto {
        return TaskDto(
            id = this[TasksTable.taskId],
            name = this[TasksTable.name],
            icon = this[TasksTable.icon],
            color = this[TasksTable.color],
            type = this[TasksTable.type],
            note = this[TasksTable.note],
            isComplete = this[TasksTable.isComplete],
            date = this[TasksTable.date]
        )
    }
}