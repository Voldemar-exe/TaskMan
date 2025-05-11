package com.example.db.repository

import com.example.db.dao.UserDAO
import com.example.shared.dto.TaskDto


interface TaskRepository {
    suspend fun getAllTasksForUser(login: String): List<TaskDto>
    suspend fun getTasksWithoutGroupForUser(login: String): List<TaskDto>
    suspend fun createTask(userDao: UserDAO, task: TaskDto): Int
    suspend fun updateTask(login: String, task: TaskDto): Boolean
    suspend fun deleteTask(login: String, id: Int): Boolean
}

